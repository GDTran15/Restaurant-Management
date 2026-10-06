package com.duong.RestaurantManagement.food.service;

import com.duong.RestaurantManagement.food.dto.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.food.dto.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.common.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.food.exception.FoodCategoryNotEmptyException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.FoodCategory;
import com.duong.RestaurantManagement.food.repository.FoodCategoryMapRepo;
import com.duong.RestaurantManagement.food.repository.FoodCategoryRepo;

import com.duong.RestaurantManagement.food.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class FoodCategoryServiceImp implements FoodCategoryService {

    private final FoodCategoryRepo foodCategoryRepo;
    private final FoodCategoryMapRepo foodCategoryMapRepo;

    @Override
    public void createNewFoodCategory(String foodCategoryName) {
        boolean categoryExist = foodCategoryRepo.existsByFoodCategoryName(foodCategoryName);
        if (categoryExist) {
            throw new DuplicateResourceException(Map.of(
                    "foodCategory", "Food category already exist"
            ));
        }
        FoodCategory foodCategory = FoodCategory.builder()
                .foodCategoryName(foodCategoryName).build();


        foodCategoryRepo.save(foodCategory);

    }

    @Override
    public List<GetFoodCategoryDTO>  getAllCategory() {
        return foodCategoryRepo.findAll()
                .stream()
                .map(
                        foodCategory -> {
                            return new GetFoodCategoryDTO(
                                    foodCategory.getFoodCategoryId(),
                                    foodCategory.getFoodCategoryName()
                            );
                        }
                ).toList();
    }

    @Override
    public List<GetFoodCategoryAndFoodCountDTO> getCategoryAndFoodCount() {
        return  foodCategoryRepo.findAllWithFoodBelongsCount();
    }

    @Override
    public void deleteFoodCategory(Long foodCategoryId) {
       boolean exists = foodCategoryMapRepo.existsByFoodCategory_FoodCategoryId(foodCategoryId);
       if (exists) {
           throw new FoodCategoryNotEmptyException("Cannot delete this food category because some foods are still using it.");
       }
       foodCategoryRepo.deleteById(foodCategoryId);
    }

    @Override
    public void updateFoodCategoryName(String foodCategoryName, Long foodCategoryId) {
        FoodCategory foodCategory = foodCategoryRepo.findById(foodCategoryId).orElseThrow(() ->
                new ResourceNotFoundException("Food category not found"));
        foodCategory.setFoodCategoryName(foodCategoryName);
        foodCategoryRepo.save(foodCategory);

    }
}
