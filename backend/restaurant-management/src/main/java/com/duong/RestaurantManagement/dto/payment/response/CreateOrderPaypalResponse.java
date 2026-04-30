package com.duong.RestaurantManagement.dto.payment.response;

public record CreateOrderPaypalResponse(
        String paypalOrderId,
        String approvalUrl
) {
}
