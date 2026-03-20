package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.order.request.OrderItemDTO;
import com.duong.RestaurantManagement.model.Order;

import java.util.List;

public interface OrderItemService {
    double addListOfOrderItemAndGetTotalPrice(List<OrderItemDTO> orderItemDTOS, Order order);
}
