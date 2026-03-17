package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.dto.menu.request.FoodsAtIntoMenu;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetMenuAsOption;
import com.duong.RestaurantManagement.dto.menu.response.MenuDetailResponseDTO;
import com.duong.RestaurantManagement.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;


    @PostMapping()
    public ResponseEntity<String> addMenu(@RequestBody @Valid AddMenuRequestDTO addMenuRequestDTO) {
        menuService.createNewMenu(addMenuRequestDTO);
        return ResponseEntity.ok("Menu successfully added");
    }

    @PostMapping("/{menuId}/foods")
    public ResponseEntity<String> addFoodsIntoMenu(FoodsAtIntoMenu foodsAtIntoMenu, @PathVariable Long menuId) {
        menuService.addMenuItems(foodsAtIntoMenu,menuId);
        return ResponseEntity.ok("Menu successfully added");
    }
    @PutMapping("/{menuId}")
    public ResponseEntity<String> toggleMenu(
            @PathVariable Long menuId,
            @RequestParam boolean active
    ){
       String message = menuService.changeMenuActivation(menuId,active);
        return ResponseEntity.ok(message);
    }

    @GetMapping("")
    public ResponseEntity<List<GetListOfMenuDTO>> getAllMenus() {
        return ResponseEntity.ok(menuService.getMenuLists());
    }

    @GetMapping("/options")
    public ResponseEntity<List<GetMenuAsOption>> getMenuOptions() {
        return ResponseEntity.ok(menuService.getMenuForOption());
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDetailResponseDTO> getMenuDetails(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getMenuDetailsById(menuId));
    }

}
