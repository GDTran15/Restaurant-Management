package com.duong.RestaurantManagement.menu.service;

import com.duong.RestaurantManagement.menu.dto.request.AddMenuItemDTO;
import com.duong.RestaurantManagement.menu.dto.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.menu.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.menu.model.Menu;
import com.duong.RestaurantManagement.menu.model.MenuItem;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.menu.repository.MenuItemRepo;
import com.duong.RestaurantManagement.menu.repository.MenuRepo;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import com.duong.RestaurantManagement.menu.service.MenuItemsService;
import com.duong.RestaurantManagement.menu.service.MenuService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImp implements MenuItemsService {

    private final MenuItemRepo menuItemRepo;
    private final MenuService menuService;
    private final DiningSessionService diningSessionService;
    private final FoodRepo foodRepo;
    private final MenuRepo menuRepo;

    @Transactional
    @Override
    public void deleteItemOfMenu(long menuId,long foodId) {
        Menu menu = menuRepo.findByIdForUpdate(menuId)
                                    .orElseThrow(() -> new ResourceNotFoundException(menuId + ": menu not found"));
        //later add check is there any dinning session
        validateMenuCanBeModified(menu);
        menuItemRepo.deleteByMenu_MenuIdAndFood_FoodId( menuId, foodId);
    }

    @Transactional
    @Override
    public void addMenuItems(FoodsAddIntoMenu foodsAtIntoMenu, Long menuId) {
        Menu menu = menuRepo.findMenuWithMenuItemByIdForUpdating(menuId)
                            .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        validateMenuCanBeModified(menu);

        Set<Long> existedFood = menu.getMenuItems().stream()
                                    .map(menuItem -> menuItem.getFood().getFoodId())
                                    .collect(Collectors.toSet());

        Set<Long> newFoodIds = new HashSet<>(foodsAtIntoMenu.foodIdList());

        newFoodIds.removeAll(existedFood);

        if (newFoodIds.isEmpty()) return;

        List<Food> foods = foodRepo.findAllById(newFoodIds);

        if (foods.size() != newFoodIds.size()) {
            throw new ResourceNotFoundException("Food missing");
        }

        List<MenuItem> menuItems = foods.stream()
                                        .map(food -> MenuItem.builder().menu(menu)
                                                             .food(food).build()).toList();



        menuItemRepo.saveAll(menuItems);
    }

    public void validateMenuCanBeModified(Menu menu){
        if ( menu.isActivated() && diningSessionService.checkIfAnyDinningSessionActive()){
            throw new MenuModificationNotAllowedException("Menu cannot be modified while it is active and a dining session is in progress");
        }

    }

}

