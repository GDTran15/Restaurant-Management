package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.menu_items.request.AddMenuItemDTO;
import com.duong.RestaurantManagement.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.model.MenuItem;
import com.duong.RestaurantManagement.repo.FoodRepo;
import com.duong.RestaurantManagement.repo.MenuItemRepo;
import com.duong.RestaurantManagement.repo.MenuRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.MenuItemsService;
import com.duong.RestaurantManagement.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //later add check is there any dinning session
        validateMenuCanBeModified(menuId);
        menuItemRepo.deleteByMenu_MenuIdAndFood_FoodId( menuId, foodId);
    }

    @Transactional
    @Override
    public void addMenuItems(AddMenuItemDTO addMenuItemDTO){
        validateMenuCanBeModified(addMenuItemDTO.menuId());
        for (Long foodId : addMenuItemDTO.foodIdList()) {
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
        if (  menuService.checkIfMenuIsActive(menuId) && diningSessionService.checkIfAnyDinningSessionActive()){
            throw new MenuModificationNotAllowedException("Menu cannot be modified while it is active and a dining session is in progress");

        }

    }

}

