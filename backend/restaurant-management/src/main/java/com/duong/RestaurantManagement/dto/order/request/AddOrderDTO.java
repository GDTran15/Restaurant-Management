package com.duong.RestaurantManagement.dto.order.request;

import java.util.List;

public record AddOrderDTO (
        Long diningSessionId,
        List<OrderItemDTO> orderItemDTOS

) {
}
