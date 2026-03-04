package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.model.Menu;
import com.duong.RestaurantManagement.repo.MenuRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepo menuRepo;

    @Transactional
    public void createNewMenu(AddMenuRequestDTO addMenuRequestDTO) {
        boolean menuExist = menuRepo.existsByMenuName(addMenuRequestDTO.menuName());
        if (menuExist) {
            throw new DuplicateResourceException("Menu name already existed");
        }
        Menu menu = Menu.builder()
                .menuName(addMenuRequestDTO.menuName())
                .isActivated(addMenuRequestDTO.activate())
                .build();
        menuRepo.save(menu);
    }

    public String changeMenuActivation(Long menuId, boolean active) {
        Menu menu = menuRepo.findById(menuId).orElseThrow(
                () -> new NoSuchElementException("Menu not found")
        );
        menu.setActivated(active);
        menuRepo.save(menu);
        if (active) {
            return "Menu activated successfully";
        } else {
            return "Menu deactivated successfully";
        }
    }
}
