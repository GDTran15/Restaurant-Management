package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

}
