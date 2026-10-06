package com.duong.RestaurantManagement.order.dto.response;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.order.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderForAdminDTO(
        Long orderId,
        String orderNumber,
        LocalDateTime orderTime,
        OrderStatus orderStatus,
        BigDecimal orderTotalPrice,

        Long tableId,
        Integer tableNumber,
        Long diningSessionId,

        List<GetOrderItemDTO> orderItems
) {
}
