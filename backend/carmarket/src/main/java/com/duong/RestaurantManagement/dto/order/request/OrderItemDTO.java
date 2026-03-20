package com.duong.RestaurantManagement.dto.order.request;

public record OrderItemDTO(
        long foodId,
        int quantity
) {
}
