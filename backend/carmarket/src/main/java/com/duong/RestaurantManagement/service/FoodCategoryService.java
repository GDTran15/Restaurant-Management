package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.food.response.GetAllFoodCategoryDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.exception.FoodCategoryNotEmptyException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.FoodCategory;
import com.duong.RestaurantManagement.model.FoodCategoryMap;
import com.duong.RestaurantManagement.repo.FoodCategoryMapRepo;
import com.duong.RestaurantManagement.repo.FoodCategoryRepo;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodCategoryService {

    private final FoodCategoryRepo foodCategoryRepo;
    private final FoodCategoryMapRepo foodCategoryMapRepo;


    public void createNewFoodCategory(String foodCategoryName) {
        boolean categoryExist = foodCategoryRepo.existsByFoodCategoryName(foodCategoryName);
        if (categoryExist) {
            throw new DuplicateResourceException("Food category already exist");
        }
        FoodCategory foodCategory = FoodCategory.builder()
                .foodCategoryName(foodCategoryName).build();


        foodCategoryRepo.save(foodCategory);

    }

    public List<GetAllFoodCategoryDTO>  getAllCategory() {
        return foodCategoryRepo.findAll()
                .stream()
                .map(
                        foodCategory -> {
                            return new GetAllFoodCategoryDTO(
                                    foodCategory.getFoodCategoryId(),
                                    foodCategory.getFoodCategoryName()
                            );
                        }
                ).toList();
    }

    public List<GetFoodCategoryAndFoodCountDTO> getCategoryAndFoodCount() {
        return  foodCategoryRepo.findAllWithFoodBelongsCount();
    }

    public void deleteFoodCategory(Long foodCategoryId) {
       boolean exists = foodCategoryMapRepo.existsByFoodCategory_FoodCategoryId(foodCategoryId);
       if (exists) {
           throw new FoodCategoryNotEmptyException("Cannot delete this food category because some foods are still using it.");
       }
       foodCategoryRepo.deleteById(foodCategoryId);
    }

    public void updateFoodCategoryName(String foodCategoryName, Long foodCategoryId) {
        FoodCategory foodCategory = foodCategoryRepo.findById(foodCategoryId).orElseThrow(() ->
                new ResourceNotFoundException("Food category not found"));
        foodCategory.setFoodCategoryName(foodCategoryName);
        foodCategoryRepo.save(foodCategory);

    }
}
