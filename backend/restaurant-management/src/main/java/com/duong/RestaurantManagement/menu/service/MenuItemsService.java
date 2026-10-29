package com.duong.RestaurantManagement.menu.service;

import com.duong.RestaurantManagement.menu.dto.request.AddMenuItemDTO;
import com.duong.RestaurantManagement.menu.dto.request.FoodsAddIntoMenu;

public interface MenuItemsService {


    void deleteItemOfMenu(long menuId, long foodId);

    void addMenuItems(FoodsAddIntoMenu foodsAtIntoMenu, Long menuId);
}
