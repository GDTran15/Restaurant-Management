package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.dto.menu.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetMenuAsOption;
import com.duong.RestaurantManagement.dto.menu.response.MenuDetailResponseDTO;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuService {

    void createNewMenu(AddMenuRequestDTO addMenuRequestDTO);

    String changeMenuActivation(Long menuId, boolean active);

    List<GetListOfMenuDTO> getMenuLists();

    List<GetMenuAsOption> getMenuForOption();

    MenuDetailResponseDTO getMenuDetailsById(Long menuId);

    void addMenuItems(FoodsAddIntoMenu  foodsAtIntoMenu, Long menuId);

     Page<GetFoodOfMenuDTO> getFoodOfMenu(Long menuId, int page, int size, String search);

     boolean checkIfMenuIsActive(Long menuId);

    void activateMenu(Long menuId);

    void deactivateMenu(Long menuId);
}