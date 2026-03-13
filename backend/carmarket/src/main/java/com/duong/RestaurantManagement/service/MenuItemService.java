package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.food.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.exception.FoodAvailabilityException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.repo.FoodRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepo foodRepo;
    private final FoodCategoryMapService foodCategoryMapService;


    @Transactional
    public void createNewFood(AddFoodRequestDTO addFoodRequestDTO) {
        if (foodRepo.existsByFoodName(addFoodRequestDTO.foodName())){
            throw new DuplicateResourceException("This food already existed");
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
        foodCategoryMapService.mapFoodToCategory(newFood,addFoodRequestDTO.foodCategoryId());
    }

    @Transactional
    public void updateFoodInformation(UpdateFoodRequestDTO updateFoodRequestDTO, Long foodId) {
        Food food = foodRepo.findById(foodId).orElseThrow(
                () -> new ResourceNotFoundException("This food does not exist")
        );
        food.setFoodName(updateFoodRequestDTO.foodName());
        food.setAvailable(updateFoodRequestDTO.isAvailable());
        food.setPrice(updateFoodRequestDTO.price());
        food.setDescription(updateFoodRequestDTO.description());
        food.setFoodImageUrl(updateFoodRequestDTO.foodImageUrl());
        food.setQuantity(updateFoodRequestDTO.quantity());

        foodCategoryMapService.mapFoodToCategory(food,updateFoodRequestDTO.foodCategoryId());


        foodRepo.save(food);
    }


    @Transactional
    public void updateFoodAvailability(Long foodId, boolean available) {
        Food food = foodRepo.findById(foodId).orElseThrow(
                () -> new ResourceNotFoundException("This food does not exist")
        );
        if (food.getQuantity() == 0 && available) {
            throw new FoodAvailabilityException("Food is out of stock");
        }
        food.setAvailable(available);
        foodRepo.save(food);
    }



    public Page<GetFoodListDTO> getFoodList(int page, int size, String search) {
        PageRequest pageable = PageRequest.of(page, size);

         return  foodRepo.findAllFoodWithCategoryName(pageable,search);
    }


    @Transactional
    public void removeFood(Long foodId) {
        foodCategoryMapService.deleteMap(foodId);
        foodRepo.deleteById(foodId);

    }
}
