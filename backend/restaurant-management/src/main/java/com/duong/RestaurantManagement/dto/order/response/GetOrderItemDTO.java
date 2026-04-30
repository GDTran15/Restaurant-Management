package com.duong.RestaurantManagement.dto.order.response;

public record GetOrderItemDTO(
        String foodName,
        int quantity,
        double foodPrice
) {
}
