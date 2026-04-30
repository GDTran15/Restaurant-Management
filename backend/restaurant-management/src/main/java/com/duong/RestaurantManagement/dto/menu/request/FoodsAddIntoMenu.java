package com.duong.RestaurantManagement.dto.menu.request;

import java.util.List;

public record FoodsAddIntoMenu(
        List<Long> foodIdList
) {
}
