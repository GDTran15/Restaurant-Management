package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {


    void deleteByMenu_MenuIdAndFood_FoodId(Long menuId, Long foodId);

    void deleteByFood_FoodId(Long foodId);


}
