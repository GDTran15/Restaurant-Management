package com.duong.RestaurantManagement.dto.food.response;

public record GetFoodListDTO(
        Long foodId,
        String foodName,
        boolean isAvailable,
        String description,
        int quantity,
        String foodImageUrl,
        double price,
        String foodCategoryName
) {
}
