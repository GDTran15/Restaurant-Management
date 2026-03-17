package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.food.response.GetAllFoodCategoryDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO;

import java.util.List;

public interface FoodCategoryService {
    void createNewFoodCategory(String foodCategoryName);
    List<GetAllFoodCategoryDTO> getAllCategory();
    List<GetFoodCategoryAndFoodCountDTO> getCategoryAndFoodCount();
    void deleteFoodCategory(Long foodCategoryId);
    void updateFoodCategoryName(String foodCategoryName, Long foodCategoryId);
}