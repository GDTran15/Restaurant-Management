package com.duong.RestaurantManagement.menu.repository;

import com.duong.RestaurantManagement.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {


    boolean existsByFood_FoodIdAndMenu_IsActivatedTrue(Long foodId);
    boolean existsByMenu_MenuIdAndFood_FoodId(Long menuId, Long foodId);

    void deleteByMenu_MenuIdAndFood_FoodId(Long menuId, Long foodId);

    void deleteByFood_FoodId(Long foodId);


}
