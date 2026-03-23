package com.duong.RestaurantManagement.dto.order.response;

import com.duong.RestaurantManagement.dto.order.request.OrderItemDTO;
import com.duong.RestaurantManagement.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetCustomerOrderDTO(
        String orderNumber,
        LocalDateTime orderTime,
        OrderStatus orderStatus,
        List<GetOrderItemDTO> orderItemDTOS

) {
}
