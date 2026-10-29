package com.duong.RestaurantManagement.order.repository;


import com.duong.RestaurantManagement.order.model.OrderItem;
import com.duong.RestaurantManagement.order.model.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    @EntityGraph(attributePaths = {"food"} )
    List<OrderItem> findByOrder_DiningSession_DiningSessionIdAndOrder_OrderStatus(
            Long diningSessionId,
            OrderStatus orderStatus
    );



}
