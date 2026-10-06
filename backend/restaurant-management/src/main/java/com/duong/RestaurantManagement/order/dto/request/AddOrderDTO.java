package com.duong.RestaurantManagement.order.dto.request;
import jakarta.validation.constraints.*;

import java.util.List;

public record AddOrderDTO (
        @NotNull @Positive Long diningSessionId,
        @NotEmpty List<@NotNull @jakarta.validation.Valid OrderItemDTO> orderItemDTOS

) {
}
