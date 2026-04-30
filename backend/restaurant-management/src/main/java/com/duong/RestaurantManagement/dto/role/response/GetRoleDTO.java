package com.duong.RestaurantManagement.dto.role.response;

import com.duong.RestaurantManagement.model.Role;

public record GetRoleDTO(
        Role value,
        String label
) {
}
