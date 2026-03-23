package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.dto.menu.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetMenuActiveDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetMenuAsOption;
import com.duong.RestaurantManagement.dto.menu.response.MenuDetailResponseDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.model.FoodCategory;
import com.duong.RestaurantManagement.model.Menu;
import com.duong.RestaurantManagement.model.MenuItem;
import com.duong.RestaurantManagement.repo.FoodCategoryRepo;
import com.duong.RestaurantManagement.repo.FoodRepo;
import com.duong.RestaurantManagement.repo.MenuItemRepo;
import com.duong.RestaurantManagement.repo.MenuRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImp implements MenuService {

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
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        for (Long foodId : foodsAtIntoMenu.foodIdList()) {
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
        return menuRepo.getFoodOfMenu(pageable, search, menuId,search);

    }

    @Override
    public boolean checkIfMenuIsActive(Long menuId) {
        return menuRepo.existsByMenuIdAndIsActivatedTrue(menuId);
    }

    @Override
    public void activateMenu(Long menuId) {
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