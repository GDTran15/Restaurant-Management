package com.duong.RestaurantManagement.order.dto.request;
import jakarta.validation.constraints.*;

public record OrderItemDTO(
        @Positive long foodId,
        @Positive int quantity
) {
}
