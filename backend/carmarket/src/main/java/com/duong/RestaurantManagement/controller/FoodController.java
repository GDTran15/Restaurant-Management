package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.food.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.service.FoodService;
import lombok.RequiredArgsConstructor;
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
}
