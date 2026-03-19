package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.DiningSession;
import com.duong.RestaurantManagement.model.DiningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiningSessionRepo extends JpaRepository<DiningSession, Long> {

    boolean existsByDiningStatus(DiningStatus diningStatus);
}
