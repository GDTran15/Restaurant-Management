package com.duong.RestaurantManagement.dto.menu.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMenuRequestDTO(
        @NotBlank(message = "Please fill in menu name")
        String menuName,
        @NotBlank(message = "Please fill in menu description")
        String menuDescription
) {
}
