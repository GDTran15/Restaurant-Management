package com.duong.RestaurantManagement.user.dto.response;

import com.duong.RestaurantManagement.user.model.Role;

public record GetRoleDTO(
        Role value,
        String label
) {
}
