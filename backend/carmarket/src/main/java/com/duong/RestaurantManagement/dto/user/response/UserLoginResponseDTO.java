package com.duong.RestaurantManagement.dto.user.response;

import com.duong.RestaurantManagement.model.Role;

public record UserLoginResponseDTO(
        String username,
        Role role,
        String accessToken
) {
}
