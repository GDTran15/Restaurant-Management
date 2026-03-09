package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.model.FoodCategoryMap;
import com.duong.RestaurantManagement.repo.FoodCategoryMapRepo;
import com.duong.RestaurantManagement.repo.FoodCategoryRepo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FoodCategoryMapService {

    private final FoodCategoryRepo foodCategoryRepo;
    private final FoodCategoryMapRepo foodCategoryMapRepo;

    @Transactional
    public void mapFoodToCategory(Food newFood, Long foodCategoryId) {
        FoodCategoryMap foodCategoryMap = FoodCategoryMap.builder()
                .foodCategory(foodCategoryRepo.findById(foodCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("Category not found")
                ))
                .food(newFood)
                .build();
        foodCategoryMapRepo.save(foodCategoryMap);
    }

    public void deleteMap(Long foodId) {
        foodCategoryMapRepo.deleteByFood_FoodId(foodId);
    }
}
