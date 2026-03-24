package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.model.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {

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
    Page<GetFoodListDTO> findAllFoodWithFoodName(Pageable pageable,@Param("search") String search);

    @Query("""
    select new com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO(
    f.foodId,
    f.foodName,
    f.price,
    f.isAvailable
    ) from Food f
    where (lower(f.foodName) like lower(concat('%', :search, '%'))) and
    not exists (select 1 from MenuItem mi
    where mi.food.foodId = f.foodId
    and mi.menu.menuId = :menuId
    )
""")
    Page<GetFoodOfMenuDTO> getFoodsToAddInMenu(Pageable pageable,@Param("search") String search,@Param("menuId") Long menuId);

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
        ) from Food f
         join f.menuItems mi
        left join f.foodCategoryMaps fcm
        where mi.menu.menuId = :menuId
        order by fcm.foodCategory.foodCategoryName asc
""")
    Page<GetFoodListDTO> getFoodsByMenuId(@Param("menuId") Long menuId, Pageable pageable);

    @Modifying
    @Query("""
    update Food f
    set f.quantity = f.quantity - :quantity
    where f.foodId = :foodId and f.quantity >= :quantity
""")
    int decreaseFoodQuantity(@Param("foodId") Long foodId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("""
    update Food f
    set f.quantity = f.quantity + :quantity
    where f.foodId = :foodId
""")
    int updateFoodQuantityAfterOrderCancel(@Param("foodId") Long foodId, @Param("quantity") Integer quantity);
}
