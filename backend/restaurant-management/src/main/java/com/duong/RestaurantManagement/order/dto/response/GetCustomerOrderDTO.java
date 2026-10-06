package com.duong.RestaurantManagement.order.dto.response;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.order.dto.request.OrderItemDTO;
import com.duong.RestaurantManagement.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetCustomerOrderDTO(
        Long orderId,
        String orderNumber,
        LocalDateTime orderTime,
        OrderStatus orderStatus,
        BigDecimal orderTotalPrice,
        List<GetOrderItemDTO> orderItemDTOS

) {
}
