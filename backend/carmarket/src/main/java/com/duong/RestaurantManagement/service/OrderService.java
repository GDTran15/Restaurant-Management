package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;

public interface OrderService {
    void addOrder(AddOrderDTO addOrderDTO);
}
