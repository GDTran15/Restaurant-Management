package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.food.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.service.FoodService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping()
    public ResponseEntity<String> addFood(@RequestBody AddFoodRequestDTO addFoodRequestDTO) {
        foodService.createNewFood(addFoodRequestDTO);
        return ResponseEntity.ok("Successfully added food");
    }

    @PutMapping("/{foodId}")
    public ResponseEntity<String> updateFood(@RequestBody UpdateFoodRequestDTO updateFoodRequestDTO, @PathVariable Long foodId) {
        foodService.updateFoodInformation(updateFoodRequestDTO,foodId);
        return ResponseEntity.ok("Successfully updated food");
    }

    @PatchMapping("/{foodId}/available")
    public ResponseEntity<String> changeFoodAvailability(@PathVariable Long foodId, @RequestParam boolean available) {
        foodService.updateFoodAvailability(foodId,available);
        return ResponseEntity.ok("Successfully changed available food");
    }

    @GetMapping
    public ResponseEntity<Page<GetFoodListDTO>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "") String search

    ) {

        return ResponseEntity.ok(foodService.getFoodList(page, size, search));
    }

    @GetMapping("/for-menu")
    public ResponseEntity<Page<GetFoodOfMenuDTO>> getFoodListToAddIntoMenu(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam Long menuId
    ){
        return ResponseEntity.ok(foodService.getFoodListToAddIntoMenu(page, size, search,menuId));
    }


    @DeleteMapping("/{foodId}")
    public ResponseEntity<String> deleteFood(@PathVariable Long foodId) {
        foodService.removeFood(foodId);
        return ResponseEntity.ok("Successfully deleted food");
    }
}
