package com.duong.RestaurantManagement.payment.dto.response;

public record CaptureOrderPaypalResponse(
        String paypalOrderId,
        String status,
        String description
) {
}
