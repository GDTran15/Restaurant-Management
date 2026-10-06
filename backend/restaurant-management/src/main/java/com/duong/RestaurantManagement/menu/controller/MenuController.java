package com.duong.RestaurantManagement.menu.controller;

import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.menu.dto.request.FoodsAddIntoMenu;
import com.duong.RestaurantManagement.menu.dto.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetMenuActiveDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetMenuAsOption;
import com.duong.RestaurantManagement.menu.dto.response.MenuDetailResponseDTO;
import com.duong.RestaurantManagement.menu.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
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

    @GetMapping("/{menuId}/foods")
    public ResponseEntity<Page<GetFoodOfMenuDTO>> getFoodOfMenu(@PathVariable Long menuId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "9") int size,
                                                                @RequestParam(defaultValue = "") String search
    ) {
        return ResponseEntity.ok(menuService.getFoodOfMenu(menuId,page,size,search));
    }

    @GetMapping("/active")
    public ResponseEntity<GetMenuActiveDTO> getMenuForCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size

    ) {
        return ResponseEntity.ok(menuService.getMenuActiveWithItems(page,size));
    }

    @PatchMapping("/{menuId}/activate")
    public ResponseEntity<Void> activateMenu(@PathVariable Long menuId) {
        menuService.activateMenu(menuId);
         return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{menuId}/deactivate")
    public ResponseEntity<String> deactivateMenu(@PathVariable Long menuId) {
        menuService.deactivateMenu(menuId);
        return ResponseEntity.noContent().build();
    }


}
