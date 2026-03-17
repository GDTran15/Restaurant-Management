package com.duong.RestaurantManagement.dto.food.response;

public record GetFoodOfMenuDTO(
        Long foodId,
        String foodName,
        double foodPrice,
        boolean isAvailable
) {
}
