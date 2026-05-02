package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.payment.request.CreateOrderPaypalRequest;
import com.duong.RestaurantManagement.exception.InvoiceHasBeenPaidException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.*;
import com.duong.RestaurantManagement.model.PaymentMethod;
import com.duong.RestaurantManagement.repo.InvoiceRepo;
import com.duong.RestaurantManagement.repo.OrderRepo;
import com.duong.RestaurantManagement.repo.PaymentRepo;
import com.duong.RestaurantManagement.service.InvoiceService;
import com.duong.RestaurantManagement.service.PaymentService;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.exceptions.ErrorException;
import com.paypal.sdk.models.*;
import com.paypal.sdk.models.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {


    private final InvoiceRepo invoiceRepo;
    private final InvoiceService invoiceService;
    private final PaypalServerSdkClient paypalClient;

    private final PaymentRepo paymentRepo;





    @Override
    @Transactional
    public Order createOrder(CreateOrderPaypalRequest request, String method, String intent, String description) {
        Invoice invoice = invoiceRepo.findById(request.invoiceId()).orElseThrow(
                () -> new ResourceNotFoundException("Invoice not found")
        );

        if (invoice.getInvoiceStatus() == InvoiceStatus.PAID) {
            throw new InvoiceHasBeenPaidException("Invoice has been paid");
        }

        OrdersController ordersController = paypalClient.getOrdersController();
        String totalAmount = BigDecimal.valueOf(invoice.getTotalPay())
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();

        CreateOrderInput createOrderInput = new CreateOrderInput.Builder(
                null,
                new OrderRequest.Builder(
                        CheckoutPaymentIntent.CAPTURE,
                        Arrays.asList(
                                new PurchaseUnitRequest.Builder(
                                        new AmountWithBreakdown.Builder(
                                                request.currencyCode(),
                                                totalAmount
                                        )
                                                .build()
                                ).referenceId(invoice.getInvoiceId().toString())
                                        .build()
                        )
                )
                        .build()
        )
                .prefer("return=representation")
                .build();

        try {
            Order order =  ordersController.createOrder(createOrderInput).getResult();

            BigDecimal amount = new BigDecimal(
                    order.getPurchaseUnits()
                            .getFirst()
                            .getAmount()
                            .getValue()
            ).setScale(2, RoundingMode.HALF_UP);

            Payment payment = Payment.builder()
                    .amount(amount)
                    .paidAt(null)
                    .paypalOrderId(order.getId())
                    .paymentStatus(PaymentStatus.PENDING)
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .invoice(invoice)
                    .build();
            paymentRepo.save(payment);


            return order;

        } catch (IOException | ApiException e) {
            throw new RuntimeException("fail to create paypal order", e);
        }


    }

        @Override
        @Transactional
        public Order captureOrder(String orderId) {
            CaptureOrderInput captureOrderInput = new CaptureOrderInput.Builder(
                    orderId,
                    null
            )
                    .prefer("return=representation")
                    .build();
            OrdersController ordersController = paypalClient.getOrdersController();
            try {
                Order order = ordersController.captureOrder(captureOrderInput).getResult();
                OrdersCapture ordersCapture = order.getPurchaseUnits()
                        .getFirst()
                        .getPayments()
                        .getCaptures()
                        .getFirst();
                String orderCaptureStatus = String.valueOf(order.getStatus());
                if (!"COMPLETED".equals(orderCaptureStatus)) {
                    throw new RuntimeException("Order status is not COMPLETED");
                }
                Payment payment = paymentRepo.findByPaypalOrderId(orderId).orElseThrow(
                        () -> new ResourceNotFoundException("Payment not found")
                );
                payment.setPaidAt(LocalDateTime.now());
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                paymentRepo.save(payment);
                invoiceService.markInvoiceAsPaid(payment.getInvoice());

                return order;

            } catch (IOException | ApiException e) {
                throw new RuntimeException("fail to capture paypal order", e);
            }
        }


}
