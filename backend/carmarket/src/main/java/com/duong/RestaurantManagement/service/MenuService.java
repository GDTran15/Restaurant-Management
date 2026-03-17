package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.dto.menu.request.FoodsAtIntoMenu;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetMenuAsOption;
import com.duong.RestaurantManagement.dto.menu.response.MenuDetailResponseDTO;

import java.util.List;

public interface MenuService {

    void createNewMenu(AddMenuRequestDTO addMenuRequestDTO);

    String changeMenuActivation(Long menuId, boolean active);

    List<GetListOfMenuDTO> getMenuLists();

    List<GetMenuAsOption> getMenuForOption();

    MenuDetailResponseDTO getMenuDetailsById(Long menuId);

    void addMenuItems(FoodsAtIntoMenu foodsAtIntoMenu, Long menuId);
}