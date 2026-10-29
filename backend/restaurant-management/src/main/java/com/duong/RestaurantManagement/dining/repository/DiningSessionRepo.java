package com.duong.RestaurantManagement.dining.repository;

import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.table.model.RestaurantTable;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DiningSessionRepo extends JpaRepository<DiningSession, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from DiningSession e where e.diningSessionId = :id")
    Optional<DiningSession> findLockedById(@Param("id") Long id);


    Optional<DiningSession> findFirstByRestaurantTable_TableQrCodeValueAndDiningStatusNot(String token, DiningStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
   Optional<DiningSession> findFirstByRestaurantTable_RestaurantTableIdAndDiningStatusNot(Long tableId, DiningStatus status);

    boolean existsByDiningStatus(DiningStatus diningStatus);

    Optional<DiningSession> findByDiningStatusAndDiningSessionId(DiningStatus diningStatus, Long diningSessionId);

    Optional<DiningSession> findByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus diningStatus, String restaurantTableTableQrCodeValue);

    DiningSession getByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus diningStatus, String restaurantTable_tableQrCodeValue );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DiningSession> findByDiningStatusAndRestaurantTable_RestaurantTableId(
            DiningStatus status,
            Long restaurantTableId
    );}
