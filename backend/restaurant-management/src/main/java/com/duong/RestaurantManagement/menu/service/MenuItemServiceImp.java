package com.duong.RestaurantManagement.menu.service;

import com.duong.RestaurantManagement.menu.dto.request.AddMenuItemDTO;
import com.duong.RestaurantManagement.menu.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
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

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class MenuItemServiceImp implements MenuItemsService {
    private final com.duong.RestaurantManagement.menu.service.MenuStateLock menuStateLock;

    private final MenuItemRepo menuItemRepo;
    private final MenuService menuService;
    private final DiningSessionService diningSessionService;
    private final FoodRepo foodRepo;
    private final MenuRepo menuRepo;

    @Transactional
    @Override
    public void deleteItemOfMenu(long menuId,long foodId) {
        //later add check is there any dinning session
        validateMenuCanBeModified(menuId);
        menuItemRepo.deleteByMenu_MenuIdAndFood_FoodId( menuId, foodId);
    }

    @Transactional
    @Override
    public void addMenuItems(AddMenuItemDTO addMenuItemDTO){
        validateMenuCanBeModified(addMenuItemDTO.menuId());
        for (Long foodId : addMenuItemDTO.foodIdList()) {
            if (menuItemRepo.existsByMenu_MenuIdAndFood_FoodId(addMenuItemDTO.menuId(), foodId)) continue;
            Food food = foodRepo.findById(foodId)
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

            MenuItem menuItem = MenuItem.builder()
                    .menu(menuRepo.findById(addMenuItemDTO.menuId())
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Menu not found")
                            ))
                    .food(food)
                    .build();
            menuItemRepo.save(menuItem);
        }
    }

    public void validateMenuCanBeModified(Long menuId){
        menuStateLock.acquire();
        if (!menuRepo.existsById(menuId)) throw new ResourceNotFoundException("Menu not found");
        if (  menuService.checkIfMenuIsActive(menuId) && diningSessionService.checkIfAnyDinningSessionActive()){
            throw new MenuModificationNotAllowedException("Menu cannot be modified while it is active and a dining session is in progress");

        }

    }

}

