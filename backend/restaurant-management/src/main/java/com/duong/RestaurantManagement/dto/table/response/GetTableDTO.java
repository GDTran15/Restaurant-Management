package com.duong.RestaurantManagement.dto.table.response;

public record GetTableDTO(
        Long restaurantTableId,
        int restaurantTableNumber,
        boolean restaurantTableStatus,
        int capacity
) {
}
