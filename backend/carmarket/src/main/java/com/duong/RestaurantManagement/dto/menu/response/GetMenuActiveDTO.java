package com.duong.RestaurantManagement.dto.menu.response;

import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.model.Food;

import java.util.List;

public record GetMenuActiveDTO(
        String menuName,
        List<GetFoodCategoryDTO> foodCategory,
        List<GetFoodListDTO> foods
) {
}
