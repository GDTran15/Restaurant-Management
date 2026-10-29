package com.duong.RestaurantManagement.order.dto.response;

import com.duong.RestaurantManagement.order.model.OrderStatus;

public record OrderStatusEvent(
        Long orderId,
        String oldStatus,
        String newStatus,
        Long diningSessionId
) {
}
