package com.duong.RestaurantManagement.food.dto.request;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

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
        @PositiveOrZero
        int quantity,
        @NotBlank
        String foodImageUrl,
        @NotNull
        @Positive
        @Digits(integer = 17, fraction = 2) BigDecimal price,
        @NotNull
        Long foodCategoryId

) {
}
