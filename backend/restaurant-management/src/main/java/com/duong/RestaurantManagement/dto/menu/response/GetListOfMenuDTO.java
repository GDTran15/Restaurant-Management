package com.duong.RestaurantManagement.dto.menu.response;

public record GetListOfMenuDTO(
        Long menuId,
        String menuName,
        String manuDescription,
        boolean isAvailable,
        Long numberOfFoods
) {
}
