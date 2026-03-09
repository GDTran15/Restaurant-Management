package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.FoodCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryMapRepo extends JpaRepository<FoodCategoryMap, Long> {
    boolean existsByFoodCategory_FoodCategoryId(Long foodCategoryFoodCategoryId);

    void deleteByFood_FoodId(Long foodFoodId);
}
