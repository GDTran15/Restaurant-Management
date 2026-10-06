package com.duong.RestaurantManagement.food.service;


import com.duong.RestaurantManagement.food.dto.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodCategoryDTO;

import java.util.List;

public interface FoodCategoryService {
    void createNewFoodCategory(String foodCategoryName);
    List<GetFoodCategoryDTO> getAllCategory();
    List<GetFoodCategoryAndFoodCountDTO> getCategoryAndFoodCount();
    void deleteFoodCategory(Long foodCategoryId);
    void updateFoodCategoryName(String foodCategoryName, Long foodCategoryId);
}