package com.duong.RestaurantManagement.order.dto.response;

import com.duong.RestaurantManagement.order.model.OrderStatus;

public record GetOrderNumberForEachStatus(
        OrderStatus orderStatus,
        long count
) {
}
