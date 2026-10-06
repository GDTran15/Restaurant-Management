package com.duong.RestaurantManagement.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CashPaymentRequest(
        @NotNull(message = "Invoice is required")
        Long invoiceId,

        @NotNull(message = "Enter cash amount")
        @Positive(message = "Cash amount must be greater than 0")
        BigDecimal amountReceived
) {
}
