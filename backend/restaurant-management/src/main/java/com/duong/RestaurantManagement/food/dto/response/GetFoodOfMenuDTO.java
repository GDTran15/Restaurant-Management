package com.duong.RestaurantManagement.food.dto.response;

import java.math.BigDecimal;

public record GetFoodOfMenuDTO(
        Long foodId,
        String foodName,
        BigDecimal foodPrice,
        boolean isAvailable
) {
}
