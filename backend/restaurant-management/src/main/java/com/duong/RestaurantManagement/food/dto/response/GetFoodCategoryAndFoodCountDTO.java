package com.duong.RestaurantManagement.food.dto.response;

public record GetFoodCategoryAndFoodCountDTO(
        Long foodCategoryId,
        String foodCategoryName,
        Long numberOfFoodBelongToThisCategory
) {
}
