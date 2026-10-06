package com.duong.RestaurantManagement.food.dto.response;

import java.math.BigDecimal;

public record GetFoodListDTO(
        Long foodId,
        String foodName,
        boolean isAvailable,
        String description,
        int quantity,
        String foodImageUrl,
        BigDecimal price,
        String foodCategoryName
) {
}
