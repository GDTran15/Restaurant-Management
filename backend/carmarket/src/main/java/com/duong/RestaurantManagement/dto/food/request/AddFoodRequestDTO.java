package com.duong.RestaurantManagement.dto.food.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.aspectj.weaver.ast.Not;

public record AddFoodRequestDTO(
        @NotBlank(message = "Food name must not be empty")
        String foodName,

        @NotNull(message = "Food availability must be specified")
        boolean isAvailable,

        @NotBlank(message = "Description must not be empty")
        String description,

        @NotNull(message = "Quantity must not be null")
        @Positive(message = "Quantity must be greater than 0")
        int quantity,

        @NotBlank(message = "Food image URL must not be empty")
        String foodImageUrl,

        @NotNull(message = "Price must not be null")
        @Positive(message = "Price must be greater than 0")
        double price,

        @NotNull(message = "Food category must be selected")
        Long foodCategoryId,

        @NotNull(message = "Menu must be selected")
        Long menuId


) {
}
