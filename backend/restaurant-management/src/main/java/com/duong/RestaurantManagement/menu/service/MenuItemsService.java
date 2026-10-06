package com.duong.RestaurantManagement.menu.service;

import com.duong.RestaurantManagement.menu.dto.request.AddMenuItemDTO;

public interface MenuItemsService {


    void deleteItemOfMenu(long menuId, long foodId);

    void addMenuItems(AddMenuItemDTO addMenuItemDTO);
}
