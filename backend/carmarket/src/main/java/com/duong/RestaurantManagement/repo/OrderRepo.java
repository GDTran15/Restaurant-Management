package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Order;
import com.duong.RestaurantManagement.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {



}
