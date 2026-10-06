package com.duong.RestaurantManagement.order.service;

import java.math.BigDecimal;


import com.duong.RestaurantManagement.order.dto.request.OrderItemDTO;
import com.duong.RestaurantManagement.order.model.Order;

import java.util.List;

public interface OrderItemService {
    BigDecimal addListOfOrderItemAndGetTotalPrice(List<OrderItemDTO> orderItemDTOS, Order order);
}
