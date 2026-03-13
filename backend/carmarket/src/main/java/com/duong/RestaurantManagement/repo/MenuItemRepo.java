package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepo extends JpaRepository<Menu, Long> {

    boolean existsByFoodName(String foodName);


    @Query("""
        select new com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO(
        f.foodId,
        f.foodName,
        f.isAvailable,
        f.description,
        f.quantity,
        f.foodImageUrl,
        f.price,
        fcm.foodCategory.foodCategoryName
        ) from Food f left join f.foodCategoryMaps fcm
         where lower(f.foodName) like lower(concat('%', :search, '%'))
           
""")
    Page<GetFoodListDTO> findAllFoodWithCategoryName(Pageable pageable,@Param("search") String search);
}
