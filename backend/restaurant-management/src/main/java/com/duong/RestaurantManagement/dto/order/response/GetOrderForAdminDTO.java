package com.duong.RestaurantManagement.dto.order.response;

import com.duong.RestaurantManagement.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderForAdminDTO(
        Long orderId,
        String orderNumber,
        LocalDateTime orderTime,
        OrderStatus orderStatus,
        double orderTotalPrice,

        Long tableId,
        Integer tableNumber,
        Long diningSessionId,

        List<GetOrderItemDTO> orderItems
) {
}
