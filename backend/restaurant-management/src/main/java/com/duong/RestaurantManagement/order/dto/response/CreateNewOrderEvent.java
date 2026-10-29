package com.duong.RestaurantManagement.order.dto.response;

public record CreateNewOrderEvent(
        Long orderId,
        Long diningSessionId
) {
}
