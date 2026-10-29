package com.duong.RestaurantManagement.table.repository;

import com.duong.RestaurantManagement.table.dto.response.GetTableDTO;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.table.model.RestaurantTable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantTableRepo extends JpaRepository<RestaurantTable, Long> {
    boolean existsByRestaurantTableNumber(int restaurantTableNumber);

    boolean existsByTableQrCodeValue(String tableQrCodeValue);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Optional<RestaurantTable> findByTableQrCodeValue(String tableQrCodeValue);


    boolean existsByTableQrCodeValueAndRestaurantTableStatusTrue(String tableQrCodeValue);
}
