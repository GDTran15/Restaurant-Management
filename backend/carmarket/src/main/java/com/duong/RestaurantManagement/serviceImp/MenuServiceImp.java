package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.dto.menu.request.FoodsAtIntoMenu;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetMenuAsOption;
import com.duong.RestaurantManagement.dto.menu.response.MenuDetailResponseDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.model.Menu;
import com.duong.RestaurantManagement.model.MenuItem;
import com.duong.RestaurantManagement.repo.FoodRepo;
import com.duong.RestaurantManagement.repo.MenuItemRepo;
import com.duong.RestaurantManagement.repo.MenuRepo;
import com.duong.RestaurantManagement.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImp implements MenuService {

    private final MenuRepo menuRepo;
    private final FoodRepo foodRepo;
    private final MenuItemRepo menuItemRepo;

    @Override
    @Transactional
    public void createNewMenu(AddMenuRequestDTO addMenuRequestDTO) {
        boolean menuExist = menuRepo.existsByMenuName(addMenuRequestDTO.menuName());

        if (menuExist) {
            throw new DuplicateResourceException("Menu name already existed");
        }

        Menu menu = Menu.builder()
                .menuName(addMenuRequestDTO.menuName())
                .isActivated(false)
                .menuDesc(addMenuRequestDTO.menuDescription())
                .build();

        menuRepo.save(menu);
    }

    @Override
    @Transactional
    public String changeMenuActivation(Long menuId, boolean active) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        menu.setActivated(active);
        menuRepo.save(menu);

        return active ? "Menu activated successfully" : "Menu deactivated successfully";
    }

    @Override
    public List<GetListOfMenuDTO> getMenuLists() {
        return menuRepo.getListOfMenu();
    }

    @Override
    public List<GetMenuAsOption> getMenuForOption() {
        return menuRepo.findAll()
                .stream()
                .map(menu -> new GetMenuAsOption(
                        menu.getMenuId(),
                        menu.getMenuName()
                ))
                .toList();
    }

    @Override
    public MenuDetailResponseDTO getMenuDetailsById(Long menuId) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        List<GetFoodOfMenuDTO> foodList = menu.getMenuItems()
                .stream()
                .map(menuItem -> new GetFoodOfMenuDTO(
                        menuItem.getFood().getFoodId(),
                        menuItem.getFood().getFoodName(),
                        menuItem.getFood().getPrice(),
                        menuItem.getFood().isAvailable()
                ))
                .toList();

        return new MenuDetailResponseDTO(
                menu.getMenuId(),
                menu.getMenuName(),
                menu.getMenuDesc(),
                menu.isActivated(),
                foodList
        );
    }

    @Override
    @Transactional
    public void addMenuItems(FoodsAtIntoMenu foodsAtIntoMenu, Long menuId) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        for (Long foodId : foodsAtIntoMenu.foods()) {
            Food food = foodRepo.findById(foodId)
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

            MenuItem menuItem = MenuItem.builder()
                    .menu(menu)
                    .food(food)
                    .build();

            menuItemRepo.save(menuItem);
        }
    }
}