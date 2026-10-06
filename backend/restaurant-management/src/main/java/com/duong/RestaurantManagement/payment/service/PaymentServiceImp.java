package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.invoice.model.InvoiceStatus;
import com.duong.RestaurantManagement.invoice.repository.InvoiceRepo;
import com.duong.RestaurantManagement.invoice.service.InvoiceService;
import com.duong.RestaurantManagement.payment.dto.request.*;
import com.duong.RestaurantManagement.payment.model.*;
import com.duong.RestaurantManagement.payment.model.PaymentMethod;
import com.duong.RestaurantManagement.payment.repository.PaymentRepo;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {
    private final InvoiceRepo invoiceRepo;
    private final InvoiceService invoiceService;
    private final PaypalServerSdkClient paypalClient;
    private final PaymentRepo paymentRepo;
    private final TransactionTemplate transactions;
    @Value("${app.currency}") private String currency;
    @Value("${app.frontend-url}") private String frontendUrl;

    @Override
    public Order createOrder(CreateOrderPaypalRequest request, String method, String intent, String description) {
        validateReturnUrl(request.returnUrl());
        validateReturnUrl(request.cancelUrl());
        if (currency == null || !currency.matches("[A-Z]{3}") || java.util.Currency.getInstance(currency).getDefaultFractionDigits() != 2)
            throw new IllegalArgumentException("Configure a supported two-decimal restaurant currency");
        if (request.currencyCode() != null && !currency.equals(request.currencyCode()))
            throw new IllegalArgumentException("Currency does not match restaurant currency");
        return transactions.execute(status -> {
            Invoice invoice = lockedInvoice(request.invoiceId());
            requireUnpaid(invoice);
            requireNoCapture(invoice.getInvoiceId());
            var pending = paymentRepo.findByInvoice_InvoiceIdAndPaymentStatus(invoice.getInvoiceId(), PaymentStatus.PENDING);
            try {
                if (!pending.isEmpty()) return paypalClient.getOrdersController()
                        .getOrder(new GetOrderInput.Builder(pending.getFirst().getPaypalOrderId()).build()).getResult();
                if (invoice.getTotalPay().signum() <= 0) throw new IllegalArgumentException("PayPal requires a positive invoice total");
                var input = new CreateOrderInput.Builder(null,
                        new OrderRequest.Builder(CheckoutPaymentIntent.CAPTURE, List.of(
                                new PurchaseUnitRequest.Builder(new AmountWithBreakdown.Builder(currency,
                                        invoice.getTotalPay().toPlainString()).build())
                                        .referenceId(invoice.getInvoiceId().toString()).build()))
                                .applicationContext(new OrderApplicationContext.Builder()
                                        .returnUrl(request.returnUrl()).cancelUrl(request.cancelUrl()).build()).build())
                        .prefer("return=representation").build();
                Order order = paypalClient.getOrdersController().createOrder(input).getResult();
                paymentRepo.save(Payment.builder().amount(invoice.getTotalPay()).currency(currency)
                        .paypalOrderId(order.getId()).paymentStatus(PaymentStatus.PENDING)
                        .paymentMethod(PaymentMethod.PAYPAL).invoice(invoice).build());
                return order;
            } catch (IOException | ApiException ex) {
                throw new PaymentProviderException("Unable to create PayPal order", ex);
            }
        });
    }

    @Override
    public Order captureOrder(String orderId) {
        // Commit the reservation before making any charge. Ambiguous provider
        // failures keep CAPTURING so cash settlement cannot double-charge.
        Payment reserved = transactions.execute(status -> {
            Long invoiceId = paymentRepo.findInvoiceIdByPaypalOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            Invoice invoice = lockedInvoice(invoiceId);
            Payment payment = paymentRepo.findByPaypalOrderId(orderId).orElseThrow();
            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) return payment;
            requireUnpaid(invoice);
            if (payment.getPaymentStatus() != PaymentStatus.PENDING && payment.getPaymentStatus() != PaymentStatus.CAPTURING)
                throw new InvalidOrderStateException("Payment attempt has been cancelled");
            if (payment.getAmount().compareTo(invoice.getTotalPay()) != 0)
                throw new InvalidOrderStateException("Invoice amount changed");
            payment.setPaymentStatus(PaymentStatus.CAPTURING);
            return payment;
        });
        if (reserved.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return new Order.Builder().id(orderId).status(com.paypal.sdk.models.OrderStatus.COMPLETED).build();
        }
        try {
            var orders = paypalClient.getOrdersController();
            Order order = orders.getOrder(new GetOrderInput.Builder(orderId).build()).getResult();
            if (!"COMPLETED".equals(String.valueOf(order.getStatus()))) {
                order = orders.captureOrder(new CaptureOrderInput.Builder(orderId, null)
                        .paypalRequestId("capture-" + reserved.getPaymentId())
                        .prefer("return=representation").build()).getResult();
            }
            verifyCapture(order, reserved);
            transactions.executeWithoutResult(status -> {
                Long invoiceId = paymentRepo.findInvoiceIdByPaypalOrderId(orderId).orElseThrow();
                Invoice invoice = lockedInvoice(invoiceId);
                Payment payment = paymentRepo.findByPaypalOrderId(orderId).orElseThrow();
                if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) return;
                requireUnpaid(invoice);
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());
                invoiceService.markInvoiceAsPaid(invoice);
            });
            return order;
        } catch (IOException | ApiException ex) {
            throw new PaymentProviderException("Capture outcome is unresolved; retry this payment to reconcile", ex);
        }
    }

    static void verifyCapture(Order order, Payment payment) {
        if (!payment.getPaypalOrderId().equals(order.getId()) || !"COMPLETED".equals(String.valueOf(order.getStatus()))
                || order.getPurchaseUnits() == null || order.getPurchaseUnits().size() != 1)
            throw new InvalidOrderStateException("PayPal capture is not complete");
        var payments = order.getPurchaseUnits().getFirst().getPayments();
        if (payments == null || payments.getCaptures() == null || payments.getCaptures().size() != 1)
            throw new InvalidOrderStateException("Unexpected PayPal capture count");
        var capture = payments.getCaptures().getFirst();
        if (!"COMPLETED".equals(String.valueOf(capture.getStatus())) || capture.getAmount() == null
                || !payment.getCurrency().equals(capture.getAmount().getCurrencyCode())
                || payment.getAmount().compareTo(new BigDecimal(capture.getAmount().getValue())) != 0)
            throw new InvalidOrderStateException("PayPal captured amount or currency does not match the invoice");
    }

    @Override
    public Payment createCashPayment(CashPaymentRequest request) {
        return transactions.execute(status -> {
            Invoice invoice = lockedInvoice(request.invoiceId());
            requireUnpaid(invoice);
            requireNoCapture(invoice.getInvoiceId());
            BigDecimal received = request.amountReceived();
            if (received == null || received.signum() < 0 || received.scale() > 2 || received.compareTo(invoice.getTotalPay()) < 0)
                throw new IllegalArgumentException("Cash received must cover the invoice and have at most two decimals");
            for (Payment pending : paymentRepo.findByInvoice_InvoiceIdAndPaymentStatus(invoice.getInvoiceId(), PaymentStatus.PENDING))
                pending.setPaymentStatus(PaymentStatus.CANCELLED);
            paymentRepo.flush();
            Payment payment = paymentRepo.save(Payment.builder().amount(invoice.getTotalPay()).currency(currency)
                    .paidAt(LocalDateTime.now()).paymentStatus(PaymentStatus.COMPLETED)
                    .paymentMethod(PaymentMethod.CASH).invoice(invoice)
                    .cashChange(received.subtract(invoice.getTotalPay())).build());
            invoiceService.markInvoiceAsPaid(invoice);
            return payment;
        });
    }
    private Invoice lockedInvoice(Long id) {
        return invoiceRepo.findLockedById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }
    private void requireUnpaid(Invoice invoice) {
        if (invoice.getInvoiceStatus() != InvoiceStatus.UNPAID) throw new InvalidOrderStateException("Invoice cannot be paid");
    }
    private void requireNoCapture(Long id) {
        if (paymentRepo.existsByInvoice_InvoiceIdAndPaymentStatus(id, PaymentStatus.CAPTURING))
            throw new InvalidOrderStateException("A PayPal capture must be reconciled first");
    }
    private void validateReturnUrl(String url) {
        URI allowed = URI.create(frontendUrl);
        URI candidate = URI.create(url);
        if (!java.util.Objects.equals(allowed.getScheme(), candidate.getScheme())
                || !java.util.Objects.equals(allowed.getHost(), candidate.getHost())
                || allowed.getPort() != candidate.getPort() || candidate.getUserInfo() != null)
            throw new IllegalArgumentException("Payment return URL must use the configured frontend origin");
    }
}
