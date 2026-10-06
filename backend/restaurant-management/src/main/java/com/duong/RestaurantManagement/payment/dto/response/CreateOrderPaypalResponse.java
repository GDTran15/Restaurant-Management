package com.duong.RestaurantManagement.payment.dto.response;

public record CreateOrderPaypalResponse(
        String paypalOrderId,
        String approvalUrl
) {
}
