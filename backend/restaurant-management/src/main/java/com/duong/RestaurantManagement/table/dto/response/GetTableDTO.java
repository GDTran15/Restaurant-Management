package com.duong.RestaurantManagement.table.dto.response;

public record GetTableDTO(
        Long restaurantTableId,
        int restaurantTableNumber,
        boolean restaurantTableStatus,
        int capacity
) {
}
