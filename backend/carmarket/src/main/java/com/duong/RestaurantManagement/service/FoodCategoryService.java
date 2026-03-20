package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryDTO;

import java.util.List;

public interface FoodCategoryService {
    void createNewFoodCategory(String foodCategoryName);
    List<GetFoodCategoryDTO> getAllCategory();
    List<GetFoodCategoryAndFoodCountDTO> getCategoryAndFoodCount();
    void deleteFoodCategory(Long foodCategoryId);
    void updateFoodCategoryName(String foodCategoryName, Long foodCategoryId);
}