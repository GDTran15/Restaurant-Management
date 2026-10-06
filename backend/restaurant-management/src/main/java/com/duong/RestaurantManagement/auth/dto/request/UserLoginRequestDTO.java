package com.duong.RestaurantManagement.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(
        @NotBlank(message = "Please enter username")
        String username,
        @NotBlank(message = "Please enter password")
        String password
) {
}
