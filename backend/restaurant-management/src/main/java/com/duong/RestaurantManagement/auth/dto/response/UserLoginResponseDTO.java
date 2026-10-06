package com.duong.RestaurantManagement.auth.dto.response;

import com.duong.RestaurantManagement.user.model.Role;

public record UserLoginResponseDTO(
        String username,
        Role role,
        String accessToken
) {
}
