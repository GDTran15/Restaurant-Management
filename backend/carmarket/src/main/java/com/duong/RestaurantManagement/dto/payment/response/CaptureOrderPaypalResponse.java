package com.duong.RestaurantManagement.dto.payment.response;

public record CaptureOrderPaypalResponse(
        String paypalOrderId,
        String status,
        String description
) {
}
