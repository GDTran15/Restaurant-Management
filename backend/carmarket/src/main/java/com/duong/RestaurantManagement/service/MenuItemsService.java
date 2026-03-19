package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.menu_items.request.AddMenuItemDTO;

public interface MenuItemsService {


    void deleteItemOfMenu(long menuId, long foodId);

    void addMenuItems(AddMenuItemDTO addMenuItemDTO);
}
