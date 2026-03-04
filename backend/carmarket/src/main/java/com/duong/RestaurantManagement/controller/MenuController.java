package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.menu.request.AddMenuRequestDTO;
import com.duong.RestaurantManagement.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
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

}
