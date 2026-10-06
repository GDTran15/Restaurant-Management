package com.duong.RestaurantManagement.menu.dto.response;

import com.duong.RestaurantManagement.food.dto.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodListDTO;
import com.duong.RestaurantManagement.food.model.Food;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetMenuActiveDTO(
        String menuName,
        List<GetFoodCategoryDTO> foodCategory,
        Page<GetFoodListDTO> foods
) {
}
