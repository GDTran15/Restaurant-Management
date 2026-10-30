package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.auth.service.SessionAccess;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.dining.repository.DiningSessionRepo;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.invoice.model.InvoiceStatus;
import com.duong.RestaurantManagement.invoice.repository.InvoiceRepo;
import com.duong.RestaurantManagement.invoice.service.InvoiceService;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.order.service.OrderService;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalOrderResponse;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalCaptureOrderResponse;
import com.duong.RestaurantManagement.payment.dto.response.CreateOrderPaypalResponse;
import com.duong.RestaurantManagement.payment.dto.response.PaymentResponse;
import com.duong.RestaurantManagement.payment.exception.PaymentProviderException;
import com.duong.RestaurantManagement.payment.model.Payment;
import com.duong.RestaurantManagement.payment.model.PaymentMethod;
import com.duong.RestaurantManagement.payment.model.PaymentStatus;
import com.duong.RestaurantManagement.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepo invoiceRepository;
    private final DiningSessionRepo diningSessionRepository;
    private final SessionAccess sessionAccess;
    private final OrderService orderService;
    private final PaypalOrderClient paypalOrderClient;
    private final TransactionTemplate transaction;
    private final InvoiceService invoiceService;
    @Value("${app.currency:USD}")
    private String currency = "USD";



    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CreateOrderPaypalResponse createPaypalOrder(Long invoiceId) {
        Checkout checkout = transaction.execute(status -> reserve(invoiceId));
        if (checkout.orderId() != null) {
            return new CreateOrderPaypalResponse(checkout.orderId(), checkout.approvalUrl(), PaymentStatus.PENDING);
        }

        // Commit the reservation first. On uncertainty, keep it PENDING so
        // retries use the same request ID and frozen amount.
        PaypalOrderResponse order = paypalOrderClient.createOrder(
                invoiceId,
                checkout.amount(),
                checkout.currency(),
                checkout.requestId());

        String approvalUrl = approvalUrl(order);

        return transaction.execute(status ->
                saveOrder(invoiceId, checkout.paymentId(), order, approvalUrl));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public PaymentResponse capturePaypalOrder(String orderId) {
        PaymentResponse paymentResponse = transaction.execute(status -> {
            Payment payment = lockedPayment(orderId);
            if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
                requireCapturable(payment);
                payment.setPaymentStatus(PaymentStatus.CAPTURING);
                paymentRepository.save(payment);
            }
            return PaymentResponse.from(payment);
        });

        if (paymentResponse.paymentStatus() == PaymentStatus.COMPLETED) {
            return paymentResponse;
        }

        // Keep CAPTURING on uncertainty; retries use the same provider request ID.
        PaypalCaptureOrderResponse result = paypalOrderClient.capturePaypalOrder(orderId);
        return transaction.execute(status -> {
            Payment payment = lockedPayment(orderId);
            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) return PaymentResponse.from(payment);
            requireCapturable(payment);
            validateCapture(payment, result);
            invoiceService.markInvoiceAsPaid(payment.getInvoice());
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
            return PaymentResponse.from(payment);
        });
    }

    private Payment lockedPayment(String orderId) {
        Long invoiceId = paymentRepository.findInvoiceIdByPaypalOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        Invoice invoice = lockedInvoice(invoiceId);
        sessionAccess.requireSession(invoice.getDiningSession().getDiningSessionId());
        return paymentRepository.findByPaypalOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private void requireCapturable(Payment payment) {
        Invoice invoice = payment.getInvoice();
        DiningSession session = diningSessionRepository.findLockedById(invoice.getDiningSession().getDiningSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Dining session not found"));
        if ((payment.getPaymentStatus() != PaymentStatus.PENDING && payment.getPaymentStatus() != PaymentStatus.CAPTURING)
                || payment.getPaymentMethod() != PaymentMethod.PAYPAL
                || invoice.getInvoiceStatus() != InvoiceStatus.UNPAID
                || session.getDiningStatus() != DiningStatus.WAITING_FOR_PAYMENT
                || payment.getAmount().compareTo(invoice.getTotalPay()) != 0
                || paymentRepository.existsByInvoice_InvoiceIdAndPaymentStatus(invoice.getInvoiceId(), PaymentStatus.COMPLETED)
                || orderService.hasActiveOrderByDiningSession(session.getDiningSessionId())) {
            throw new InvalidOrderStateException("Payment is not ready for capture");
        }
    }

    private void validateCapture(Payment payment, PaypalCaptureOrderResponse result) {
        var capture = getCapture(payment, result);
        if (capture == null || capture.id() == null || capture.id().isBlank()
                || !"COMPLETED".equals(capture.status()) || capture.amount() == null
                || !payment.getCurrency().equals(capture.amount().currencyCode())) {
            throw new PaymentProviderException("PayPal capture is incomplete or has an unexpected currency");
        }
        try {
            if (payment.getAmount().compareTo(new BigDecimal(capture.amount().value())) == 0) return;
        } catch (NumberFormatException | NullPointerException ignored) {
            // Reject malformed provider amounts along with mismatched amounts.
        }
        throw new PaymentProviderException("PayPal capture amount does not match the payment");
    }

    private PaypalCaptureOrderResponse.Capture getCapture(Payment payment, PaypalCaptureOrderResponse result) {
        if (result == null || !payment.getPaypalOrderId().equals(result.id())
                || !"COMPLETED".equals(result.status()) || result.purchaseUnits() == null
                || result.purchaseUnits().size() != 1) {
            throw new PaymentProviderException("PayPal has not confirmed the payment");
        }
        var unit = result.purchaseUnits().getFirst();
        if (unit == null || !payment.getInvoice().getInvoiceId().toString().equals(unit.referenceId())
                || unit.payments() == null || unit.payments().captures() == null
                || unit.payments().captures().size() != 1) {
            throw new PaymentProviderException("PayPal capture does not match the invoice");
        }
        return unit.payments().captures().getFirst();
    }

    private Checkout reserve(Long invoiceId) {
        Invoice invoice = lockedInvoice(invoiceId);
        Long sessionId = invoice.getDiningSession().getDiningSessionId();
        sessionAccess.requireSession(sessionId);
        if (invoice.getInvoiceStatus() != InvoiceStatus.UNPAID) {
            throw new InvalidOrderStateException("Only unpaid invoices can be paid");
        }
        DiningSession session = diningSessionRepository.findLockedById(sessionId)
                                                       .orElseThrow(() -> new ResourceNotFoundException("Dining session not found"));
        if (session.getDiningStatus() != DiningStatus.WAITING_FOR_PAYMENT
                || orderService.hasActiveOrderByDiningSession(sessionId)) {
            throw new InvalidOrderStateException("Dining session is not ready for payment");
        }
        if (invoice.getTotalPay() == null || invoice.getTotalPay().signum() <= 0) {
            throw new InvalidOrderStateException("Invoice must have a positive amount");
        }
        if (paymentRepository.existsByInvoice_InvoiceIdAndPaymentStatus(invoiceId, PaymentStatus.COMPLETED)) {
            throw new InvalidOrderStateException("Invoice already has a completed payment");
        }
        Payment payment = paymentRepository.findFirstByInvoice_InvoiceIdAndPaymentStatusInOrderByPaymentIdDesc(
                invoiceId, List.of(PaymentStatus.PENDING, PaymentStatus.CAPTURING)).orElse(null);
        if (payment != null) {
            if (payment.getPaymentStatus() != PaymentStatus.PENDING || payment.getPaymentMethod() != PaymentMethod.PAYPAL
                    || payment.getAmount().compareTo(invoice.getTotalPay()) != 0) {
                throw new InvalidOrderStateException("Existing payment must be reconciled before checkout");
            }
            // Orders v2 retains create request IDs for six hours. Stop before
            // that boundary rather than risk another order after a timeout.
            if (payment.getPaypalCreateRequestId() == null || payment.getPaypalCreateRequestedAt() == null
                    || payment.getPaypalCreateRequestedAt().isBefore(Instant.now().minus(Duration.ofHours(5)))) {
                throw new InvalidOrderStateException("Checkout has expired; reconcile the existing PayPal attempt");
            }
        } else {
            payment = paymentRepository.save(
                    Payment.builder()
                    .invoice(invoice)
                           .amount(invoice.getTotalPay())
                           .currency(currency)
                    .paymentMethod(PaymentMethod.PAYPAL)
                           .paymentStatus(PaymentStatus.PENDING)
                    .paypalCreateRequestId(UUID.randomUUID()
                                               .toString())
                    .paypalCreateRequestedAt(Instant.now())
                           .build());
        }
        return new Checkout(payment.getPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaypalCreateRequestId(),
                payment.getPaypalOrderId(),
                payment.getPaypalApprovalUrl());
    }

    private CreateOrderPaypalResponse saveOrder(Long invoiceId, Long paymentId, PaypalOrderResponse order, String approvalUrl) {
        Invoice invoice = lockedInvoice(invoiceId);
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("Payment attempt not found"));

        if (!payment.getInvoice().getInvoiceId().equals(invoiceId) || payment.getPaymentStatus() != PaymentStatus.PENDING
                || invoice.getInvoiceStatus() != InvoiceStatus.UNPAID
                || payment.getAmount().compareTo(invoice.getTotalPay()) != 0
                || (payment.getPaypalOrderId() != null && !payment.getPaypalOrderId().equals(order.id()))) {
            throw new InvalidOrderStateException("Payment changed during checkout; reconcile the PayPal attempt");
        }
        payment.setPaypalOrderId(order.id());
        payment.setPaypalApprovalUrl(approvalUrl);
        paymentRepository.save(payment);
        return new CreateOrderPaypalResponse(order.id(), approvalUrl, payment.getPaymentStatus());
    }

    private Invoice lockedInvoice(Long invoiceId) {
        return invoiceRepository.findLockedById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    private String approvalUrl(PaypalOrderResponse order) {
        if (order == null || order.id() == null || order.id().isBlank() || order.status() == null
                || !List.of("CREATED", "PAYER_ACTION_REQUIRED", "APPROVED").contains(order.status())) {
            throw new PaymentProviderException("PayPal returned an invalid checkout response");
        }
        if (order.links() != null) {
            for (var link : order.links()) {
                if (link != null && ("approve".equals(link.rel()) || "payer-action".equals(link.rel()))) {
                    try {
                        URI url = URI.create(link.href());
                        if ("https".equalsIgnoreCase(url.getScheme()) && url.getHost() != null) return url.toString();
                    } catch (IllegalArgumentException | NullPointerException ignored)
                    {
                        // Report an invalid provider response below.
                    }
                }
            }
        }
        if ("APPROVED".equals(order.status())){
            return null;
        }
        throw new PaymentProviderException("PayPal did not return a checkout approval URL");
    }

    private record Checkout(Long paymentId, BigDecimal amount, String currency, String requestId,
                            String orderId, String approvalUrl) {}
}
