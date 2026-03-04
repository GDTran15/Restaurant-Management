package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepo extends JpaRepository<Menu, Long> {

    boolean existsByMenuName(String menuName);
}
