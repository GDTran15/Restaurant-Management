package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.food.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.model.FoodCategory;
import com.duong.RestaurantManagement.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food-category")
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    @PostMapping
    public ResponseEntity<String> addFoodCategory(@RequestParam String foodCategoryName) {
         foodCategoryService.createNewFoodCategory(foodCategoryName);

        return ResponseEntity.ok("Success");
    }



}
