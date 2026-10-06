package com.duong.RestaurantManagement.food.service;

import com.duong.RestaurantManagement.food.dto.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.food.dto.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodListDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;
import org.springframework.data.domain.Page;

public interface FoodService {

    void createNewFood(AddFoodRequestDTO addFoodRequestDTO);

    void updateFoodInformation(UpdateFoodRequestDTO updateFoodRequestDTO, Long foodId);

    void updateFoodAvailability(Long foodId, boolean available);

    Page<GetFoodListDTO> getFoodList(int page, int size, String search);

    void removeFood(Long foodId);

    Page<GetFoodOfMenuDTO> getFoodListToAddIntoMenu(int page, int size, String search, Long menuId);
}