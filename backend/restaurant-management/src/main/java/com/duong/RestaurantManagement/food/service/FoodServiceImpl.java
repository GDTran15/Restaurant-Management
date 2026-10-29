package com.duong.RestaurantManagement.food.service;

import com.duong.RestaurantManagement.food.dto.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.food.dto.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodListDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.common.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.food.exception.FoodAvailabilityException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.menu.repository.MenuItemRepo;
import com.duong.RestaurantManagement.food.service.FoodService;
import com.duong.RestaurantManagement.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodServiceImpl implements FoodService {

    private final FoodRepo foodRepo;
    private final FoodCategoryMapService foodCategoryMapService;
    private final MenuService menuService;
    private final MenuItemRepo menuItemRepo;

    @Override
    @Transactional
    public void createNewFood(AddFoodRequestDTO addFoodRequestDTO) {
        if (foodRepo.existsByFoodName(addFoodRequestDTO.foodName())) {
            throw new DuplicateResourceException(Map.of(
                    "food","This food already existed"
            ));

        }

        Food newFood = Food.builder()
                .foodName(addFoodRequestDTO.foodName())
                .isAvailable(addFoodRequestDTO.isAvailable())
                .price(addFoodRequestDTO.price())
                .description(addFoodRequestDTO.description())
                .foodImageUrl(addFoodRequestDTO.foodImageUrl())
                .quantity(addFoodRequestDTO.quantity())
                .build();

        foodRepo.save(newFood);
        foodCategoryMapService.mapFoodToCategory(newFood, addFoodRequestDTO.foodCategoryId());
    }

    @Override
    @Transactional
    public void updateFoodInformation(UpdateFoodRequestDTO updateFoodRequestDTO, Long foodId) {
        Food food = foodRepo.findLockedById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("This food does not exist"));

        food.setFoodName(updateFoodRequestDTO.foodName());
        food.setAvailable(updateFoodRequestDTO.isAvailable());
        food.setPrice(updateFoodRequestDTO.price());
        food.setDescription(updateFoodRequestDTO.description());
        food.setFoodImageUrl(updateFoodRequestDTO.foodImageUrl());
        food.setQuantity(updateFoodRequestDTO.quantity());

        foodCategoryMapService.mapFoodToCategory(food, updateFoodRequestDTO.foodCategoryId());

        foodRepo.save(food);
    }

    @Override
    @Transactional
    public void updateFoodAvailability(Long foodId, boolean available) {
        Food food = foodRepo.findLockedById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("This food does not exist"));

        if (food.getQuantity() == 0 && available) {
            throw new FoodAvailabilityException("Food is out of stock");
        }

        food.setAvailable(available);
        foodRepo.save(food);
    }

    @Override
    public Page<GetFoodListDTO> getFoodList(int page, int size, String search) {
        PageRequest pageable = PageRequest.of(page, size);
        return foodRepo.findAllFoodWithFoodName(pageable, search);
    }

    @Override
    @Transactional
    public void removeFood(Long foodId) {
        menuItemRepo.deleteByFood_FoodId(foodId);

        foodCategoryMapService.deleteMap(foodId);
        foodRepo.deleteById(foodId);
    }

    @Override
    public Page<GetFoodOfMenuDTO> getFoodListToAddIntoMenu(int page, int size, String search, Long menuId) {
        PageRequest pageable = PageRequest.of(page, size);
        return foodRepo.getFoodsToAddInMenu(pageable, search, menuId);
    }
}