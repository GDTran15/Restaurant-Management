package com.duong.RestaurantManagement.dto.food.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateFoodRequestDTO(
        @NotBlank
        String foodName,
        @NotNull
        boolean isAvailable,
        @NotBlank
        String description,
        @NotNull
        @Positive
        int quantity,
        @NotBlank
        String foodImageUrl,
        @NotNull
        @Positive
        double price,
        @NotNull
        Long foodCategoryId

) {
}
