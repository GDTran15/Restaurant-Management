package com.duong.RestaurantManagement.menu.dto.response;

import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;

import java.util.List;

public record MenuDetailResponseDTO(
        Long menuId,
        String menuName,
        String menuDescription,
        boolean isAvailable

) {
}
