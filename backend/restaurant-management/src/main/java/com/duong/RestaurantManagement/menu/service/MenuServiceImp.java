package com.duong.RestaurantManagement.menu.service;

import com.duong.RestaurantManagement.food.dto.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodListDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.menu.dto.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.menu.dto.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetMenuActiveDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetMenuAsOption;
import com.duong.RestaurantManagement.menu.dto.response.MenuDetailResponseDTO;
import com.duong.RestaurantManagement.common.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.menu.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.food.model.FoodCategory;
import com.duong.RestaurantManagement.menu.model.Menu;
import com.duong.RestaurantManagement.menu.model.MenuItem;
import com.duong.RestaurantManagement.food.repository.FoodCategoryRepo;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.menu.repository.MenuItemRepo;
import com.duong.RestaurantManagement.menu.repository.MenuRepo;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import com.duong.RestaurantManagement.menu.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class MenuServiceImp implements MenuService {
    private final com.duong.RestaurantManagement.menu.service.MenuStateLock menuStateLock;

    private final MenuRepo menuRepo;
    private final FoodRepo foodRepo;
    private final MenuItemRepo menuItemRepo;
    private final DiningSessionService diningSessionService;
    private final FoodCategoryRepo foodCategoryRepo;

    @Override
    @Transactional
    public void createNewMenu(AddMenuRequestDTO addMenuRequestDTO) {
        boolean menuExist = menuRepo.existsByMenuName(addMenuRequestDTO.menuName());

        if (menuExist) {
            throw new DuplicateResourceException(
                    Map.of("menu","Menu name already existed")
            );
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
        if (active) activateMenu(menuId); else deactivateMenu(menuId);
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

        return new MenuDetailResponseDTO(
                menu.getMenuId(),
                menu.getMenuName(),
                menu.getMenuDesc(),
                menu.isActivated()
        );
    }

    @Override
    @Transactional
    public void addMenuItems(FoodsAddIntoMenu foodsAtIntoMenu, Long menuId) {
        menuStateLock.acquire();
        if (checkIfMenuIsActive(menuId) && diningSessionService.checkIfAnyDinningSessionActive())
            throw new MenuModificationNotAllowedException("Cannot edit an active menu during dining");
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        for (Long foodId : foodsAtIntoMenu.foodIdList()) {
            if (menuItemRepo.existsByMenu_MenuIdAndFood_FoodId(menuId, foodId)) continue;
            Food food = foodRepo.findById(foodId)
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

            MenuItem menuItem = MenuItem.builder()
                    .menu(menu)
                    .food(food)
                    .build();

            menuItemRepo.save(menuItem);
        }
    }

    @Override
    public Page<GetFoodOfMenuDTO> getFoodOfMenu(Long menuId, int page, int size, String search) {
        PageRequest pageable = PageRequest.of(page, size);
        return menuRepo.getFoodOfMenu(pageable, menuId, search);

    }

    @Override
    public boolean checkIfMenuIsActive(Long menuId) {
        return menuRepo.existsByMenuIdAndIsActivatedTrue(menuId);
    }

    @Override
    public void activateMenu(Long menuId) {
        menuStateLock.acquire();
        if (menuRepo.existsByMenuIdAndIsActivatedTrue(menuId)) return;
        boolean checkIfAnyMenuCurrentlyActive = menuRepo.existsByIsActivatedTrue();
        if (checkIfAnyMenuCurrentlyActive) {
            throw new MenuModificationNotAllowedException("Cannot activate menu during other menu activation");
        }
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));
        menu.setActivated(true);
        menuRepo.save(menu);
    }

    @Override
    public void deactivateMenu(Long menuId) {
        menuStateLock.acquire();

        if (diningSessionService.checkIfAnyDinningSessionActive()) {
            throw new MenuModificationNotAllowedException("Cannot deactivate menu during dining session");
        }
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));
        menu.setActivated(false);
        menuRepo.save(menu);
    }

    @Override
    public GetMenuActiveDTO getMenuActiveWithItems(int page,int size) {
        Menu menu = menuRepo.findByIsActivatedTrue().orElseThrow(
                () -> new ResourceNotFoundException("We currently don't have active menu")
        );

        PageRequest pageable = PageRequest.of(page, size);

        Page<GetFoodListDTO> getFoodListInMenu = foodRepo.getFoodsByMenuId(menu.getMenuId(),pageable);
        List<GetFoodCategoryDTO> getFoodCategoryInMenu = foodCategoryRepo.findFoodCategoryAppearInMenu(menu.getMenuId());
        return new GetMenuActiveDTO(
                menu.getMenuName(),
                getFoodCategoryInMenu,
                getFoodListInMenu

        );

    }


}