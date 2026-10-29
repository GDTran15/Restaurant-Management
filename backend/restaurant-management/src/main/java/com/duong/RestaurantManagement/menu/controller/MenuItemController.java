package com.duong.RestaurantManagement.menu.controller;

import com.duong.RestaurantManagement.menu.dto.request.AddMenuItemDTO;
import com.duong.RestaurantManagement.menu.dto.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.menu.service.MenuItemsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemsService menuItemService;


    @DeleteMapping("")
    public ResponseEntity<Void> deleteMenuItem(@RequestParam long menuId, @RequestParam long foodId) {
                    menuItemService.deleteItemOfMenu(menuId,foodId);
                    return ResponseEntity.noContent().build();

    }
    @PostMapping("/{menuId}")
    public ResponseEntity<Void> addMenuItems(@Valid @RequestBody FoodsAddIntoMenu foodsAtIntoMenu,@PathVariable Long menuId) {
        menuItemService.addMenuItems(foodsAtIntoMenu,menuId);
        return ResponseEntity.noContent().build();
    }
}

