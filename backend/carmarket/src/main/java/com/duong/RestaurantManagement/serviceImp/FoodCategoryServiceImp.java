package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.exception.FoodCategoryNotEmptyException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.FoodCategory;
import com.duong.RestaurantManagement.repo.FoodCategoryMapRepo;
import com.duong.RestaurantManagement.repo.FoodCategoryRepo;

import com.duong.RestaurantManagement.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodCategoryServiceImp implements FoodCategoryService {

    private final FoodCategoryRepo foodCategoryRepo;
    private final FoodCategoryMapRepo foodCategoryMapRepo;

    @Override
    public void createNewFoodCategory(String foodCategoryName) {
        boolean categoryExist = foodCategoryRepo.existsByFoodCategoryName(foodCategoryName);
        if (categoryExist) {
            throw new DuplicateResourceException("Food category already exist");
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
