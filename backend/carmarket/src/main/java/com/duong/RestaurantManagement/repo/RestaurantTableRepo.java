package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.table.response.GetTableDTO;
import com.duong.RestaurantManagement.model.RestaurantTable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepo extends JpaRepository<RestaurantTable, Long> {
    boolean existsByRestaurantTableNumber(int restaurantTableNumber);


}
