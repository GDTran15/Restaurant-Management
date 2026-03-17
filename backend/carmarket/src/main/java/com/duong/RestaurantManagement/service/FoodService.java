package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.food.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import org.springframework.data.domain.Page;

public interface FoodService {

    void createNewFood(AddFoodRequestDTO addFoodRequestDTO);

    void updateFoodInformation(UpdateFoodRequestDTO updateFoodRequestDTO, Long foodId);

    void updateFoodAvailability(Long foodId, boolean available);

    Page<GetFoodListDTO> getFoodList(int page, int size, String search);

    void removeFood(Long foodId);

    Page<GetFoodOfMenuDTO> getFoodListToAddIntoMenu(int page, int size, String search, Long menuId);
}