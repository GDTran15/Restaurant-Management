package com.duong.RestaurantManagement.order.dto.response;


public record OrderNewStatus(
        Long orderId,
        String orderStatus
) {
}
