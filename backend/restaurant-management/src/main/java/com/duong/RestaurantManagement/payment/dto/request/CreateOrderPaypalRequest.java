package com.duong.RestaurantManagement.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderPaypalRequest(
        @NotNull
        @Positive
        Long invoiceId) {
}
