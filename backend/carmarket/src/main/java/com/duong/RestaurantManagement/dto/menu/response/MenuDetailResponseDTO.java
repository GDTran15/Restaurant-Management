package com.duong.RestaurantManagement.dto.menu.response;

import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;

import java.util.List;

public record MenuDetailResponseDTO(
        Long menuId,
        String menuName,
        String menuDescription,
        boolean isAvailable,
        List<GetFoodOfMenuDTO> foodOfMenu
) {
}
