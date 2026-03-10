package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.RestaurantTable;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepo extends JpaRepository<RestaurantTable, Long> {
    boolean existsByRestaurantTableNumber(int restaurantTableNumber);

}
