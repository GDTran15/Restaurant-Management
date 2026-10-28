package com.duong.RestaurantManagement.payment.dto.response;

import com.duong.RestaurantManagement.payment.model.PaymentStatus;

public record CreateOrderPaypalResponse(
        String providerOrderId,
        String approvalUrl,
        PaymentStatus paymentStatus
) {
}
