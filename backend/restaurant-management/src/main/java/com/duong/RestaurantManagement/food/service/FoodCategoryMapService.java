package com.duong.RestaurantManagement.food.service;

import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.food.model.FoodCategory;
import com.duong.RestaurantManagement.food.model.FoodCategoryMap;
import com.duong.RestaurantManagement.food.repository.FoodCategoryMapRepo;
import com.duong.RestaurantManagement.food.repository.FoodCategoryRepo;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodCategoryMapService {

    private final FoodCategoryRepo foodCategoryRepo;
    private final FoodCategoryMapRepo foodCategoryMapRepo;

    @Transactional
    public void mapFoodToCategory(Food newFood, Long foodCategoryId) {
        FoodCategory category = foodCategoryRepo.findById(foodCategoryId)
                                                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        FoodCategoryMap foodCategoryMap = foodCategoryMapRepo.findByFood_FoodId(newFood.getFoodId())
                .orElseGet(() -> FoodCategoryMap.builder().food(newFood).build());
        foodCategoryMap.setFoodCategory(category);
        foodCategoryMapRepo.save(foodCategoryMap);
    }

    public void deleteMap(Long foodId) {
        foodCategoryMapRepo.deleteByFood_FoodId(foodId);
    }
}
