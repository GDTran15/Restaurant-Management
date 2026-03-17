package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {
}
