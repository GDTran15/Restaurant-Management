package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.food.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.response.GetAllFoodCategoryDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.model.FoodCategory;
import com.duong.RestaurantManagement.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food-categories")
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    @PostMapping
    public ResponseEntity<String> addFoodCategory(@RequestParam String foodCategoryName) {
         foodCategoryService.createNewFoodCategory(foodCategoryName);

        return ResponseEntity.ok("Success");
    }

    @PutMapping("/{foodCategoryId}")
    public ResponseEntity<String> updateFoodCategory(@RequestParam String foodCategoryName,@PathVariable Long foodCategoryId) {
        foodCategoryService.updateFoodCategoryName(foodCategoryName,foodCategoryId);
        return ResponseEntity.ok("Food has been updated");
    }


    @GetMapping
    public ResponseEntity<List<GetAllFoodCategoryDTO>> getFoodCategory() {
        return ResponseEntity.ok( foodCategoryService.getAllCategory()) ;
    }

    @GetMapping("/food-count")
    public ResponseEntity<List<GetFoodCategoryAndFoodCountDTO>> getFoodCategoryWithFoodCounts() {
         return ResponseEntity.ok( foodCategoryService.getCategoryAndFoodCount()) ;
    }



    @DeleteMapping("/{foodCategoryId}")
    public ResponseEntity<String> deleteFoodCategory(@PathVariable Long foodCategoryId) {
        foodCategoryService.deleteFoodCategory(foodCategoryId);
        return ResponseEntity.ok("Success");
    }

}
