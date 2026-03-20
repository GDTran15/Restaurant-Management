package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.DiningSession;
import com.duong.RestaurantManagement.model.DiningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiningSessionRepo extends JpaRepository<DiningSession, Long> {

    boolean existsByDiningStatus(DiningStatus diningStatus);

    Optional<DiningSession> findByDiningStatusAndDiningSessionId(DiningStatus diningStatus, Long diningSessionId);

    Optional<DiningSession> findByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus diningStatus, String restaurantTableTableQrCodeValue);

    DiningSession getByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus diningStatus, String restaurantTable_tableQrCodeValue );
}
