package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryDTO;
import com.duong.RestaurantManagement.model.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodCategoryRepo extends JpaRepository<FoodCategory, Long> {

    boolean existsByFoodCategoryName(String foodCategoryName);


    @Query("""
     select new com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryAndFoodCountDTO(
        fc.foodCategoryId,
        fc.foodCategoryName,
        count(fcm.food)
     )  from FoodCategory fc left join FoodCategoryMap fcm
   on fc.foodCategoryId = fcm.foodCategory.foodCategoryId
     group by fc.foodCategoryId,fc.foodCategoryName

""")
    List<GetFoodCategoryAndFoodCountDTO> findAllWithFoodBelongsCount();


    @Query("""
    select distinct new com.duong.RestaurantManagement.dto.food.response.GetFoodCategoryDTO(
    fc.foodCategoryId,
    fc.foodCategoryName
    ) from FoodCategory fc
    join fc.foodCategoryMaps fcm
    join fcm.food f
    join f.menuItems mi
    where mi.menu.menuId = :menuId
    order by fc.foodCategoryName
""")
    List<GetFoodCategoryDTO> findFoodCategoryAppearInMenu(@Param("menuId") Long menuId);
}
