package com.duong.RestaurantManagement.payment.dto.response;

import com.duong.RestaurantManagement.payment.model.Payment;
import com.duong.RestaurantManagement.payment.model.PaymentMethod;
import com.duong.RestaurantManagement.payment.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long invoiceId,
        String providerOrderId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime paidAt) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getPaymentId(),
                payment.getInvoice().getInvoiceId(),
                payment.getPaypalOrderId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getPaidAt());
    }
}
