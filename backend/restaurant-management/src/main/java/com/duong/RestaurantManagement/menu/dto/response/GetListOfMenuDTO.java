package com.duong.RestaurantManagement.menu.dto.response;

public record GetListOfMenuDTO(
        Long menuId,
        String menuName,
        String manuDescription,
        boolean isAvailable,
        Long numberOfFoods
) {
}
