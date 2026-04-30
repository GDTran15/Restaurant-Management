package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.table.response.GetTableDTO;
import com.duong.RestaurantManagement.model.DiningStatus;
import com.duong.RestaurantManagement.model.RestaurantTable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantTableRepo extends JpaRepository<RestaurantTable, Long> {
    boolean existsByRestaurantTableNumber(int restaurantTableNumber);

    boolean existsByTableQrCodeValue(String tableQrCodeValue);

    Optional<RestaurantTable> findByTableQrCodeValue(String tableQrCodeValue);


    boolean existsByTableQrCodeValueAndRestaurantTableStatusTrue(String tableQrCodeValue);
}
