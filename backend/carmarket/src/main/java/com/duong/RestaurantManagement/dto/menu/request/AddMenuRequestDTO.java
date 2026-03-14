package com.duong.RestaurantManagement.dto.menu.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMenuRequestDTO(
        @NotBlank
        String menuName,
        @NotBlank
        String menuDescription
) {
}
