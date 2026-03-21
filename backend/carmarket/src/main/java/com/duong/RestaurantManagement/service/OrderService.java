package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.model.OrderStatus;

public interface OrderService {
    void addOrder(AddOrderDTO addOrderDTO);

    void updateOrderStatus(Long orderId, OrderStatus orderStatus);

    void deleteOrder(Long orderId);
}
