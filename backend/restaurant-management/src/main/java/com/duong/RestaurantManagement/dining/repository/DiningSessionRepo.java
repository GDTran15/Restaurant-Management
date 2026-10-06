package com.duong.RestaurantManagement.dining.repository;

import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.table.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiningSessionRepo extends JpaRepository<DiningSession, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from DiningSession e where e.diningSessionId = :id")
    java.util.Optional<DiningSession> findLockedById(@org.springframework.data.repository.query.Param("id") Long id);


    java.util.Optional<DiningSession> findFirstByRestaurantTable_TableQrCodeValueAndDiningStatusNot(String token, DiningStatus status);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<DiningSession> findFirstByRestaurantTable_RestaurantTableIdAndDiningStatusNot(Long tableId, DiningStatus status);

    boolean existsByDiningStatus(DiningStatus diningStatus);

    Optional<DiningSession> findByDiningStatusAndDiningSessionId(DiningStatus diningStatus, Long diningSessionId);

    Optional<DiningSession> findByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus diningStatus, String restaurantTableTableQrCodeValue);

    DiningSession getByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus diningStatus, String restaurantTable_tableQrCodeValue );

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Optional<DiningSession> findByDiningStatusAndRestaurantTable_RestaurantTableId(
            DiningStatus status,
            Long restaurantTableId
    );}
