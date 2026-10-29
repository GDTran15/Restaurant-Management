package com.duong.RestaurantManagement.menu.service;

import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.menu.dto.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.menu.dto.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetMenuActiveDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetMenuAsOption;
import com.duong.RestaurantManagement.menu.dto.response.MenuDetailResponseDTO;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuService {

    void createNewMenu(AddMenuRequestDTO addMenuRequestDTO);

    List<GetListOfMenuDTO> getMenuLists();

    List<GetMenuAsOption> getMenuForOption();

    MenuDetailResponseDTO getMenuDetailsById(Long menuId);

    Page<GetFoodOfMenuDTO> getFoodOfMenu(Long menuId, int page, int size, String search);

    void activateMenu(Long menuId);

    void deactivateMenu(Long menuId);

    GetMenuActiveDTO getMenuActiveWithItems(int page, int size);

}