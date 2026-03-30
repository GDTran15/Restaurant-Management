package com.duong.RestaurantManagement.dto.order.response;

import com.duong.RestaurantManagement.model.OrderStatus;

public record GetOrderNumberForEachStatus(
        OrderStatus orderStatus,
        long count
) {
}
