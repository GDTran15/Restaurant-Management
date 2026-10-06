package com.duong.RestaurantManagement.order.dto.response;

import java.math.BigDecimal;

public record GetOrderItemDTO(
        String foodName,
        int quantity,
        BigDecimal itemPrice,
        BigDecimal foodPrice
) {
}
