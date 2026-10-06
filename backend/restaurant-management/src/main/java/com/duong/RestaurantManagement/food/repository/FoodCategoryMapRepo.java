package com.duong.RestaurantManagement.food.repository;

import com.duong.RestaurantManagement.food.model.FoodCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryMapRepo extends JpaRepository<FoodCategoryMap, Long> {
    boolean existsByFoodCategory_FoodCategoryId(Long foodCategoryFoodCategoryId);

    java.util.Optional<FoodCategoryMap> findByFood_FoodId(Long foodId);

    void deleteByFood_FoodId(Long foodFoodId);
}
