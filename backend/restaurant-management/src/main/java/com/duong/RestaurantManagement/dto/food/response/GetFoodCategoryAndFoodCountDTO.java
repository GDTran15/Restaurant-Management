package com.duong.RestaurantManagement.dto.food.response;

public record GetFoodCategoryAndFoodCountDTO(
        Long foodCategoryId,
        String foodCategoryName,
        Long numberOfFoodBelongToThisCategory
) {
}
