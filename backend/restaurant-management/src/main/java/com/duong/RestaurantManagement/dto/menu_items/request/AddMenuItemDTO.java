package com.duong.RestaurantManagement.dto.menu_items.request;

import java.util.List;

public record AddMenuItemDTO(
        Long menuId,
        List<Long> foodIdList
) {
}
