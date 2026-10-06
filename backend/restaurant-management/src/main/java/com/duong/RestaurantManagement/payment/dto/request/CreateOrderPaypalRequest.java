package com.duong.RestaurantManagement.payment.dto.request;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateOrderPaypalRequest(
        @NotNull @Positive Long invoiceId,
        String currencyCode,
        @NotBlank String cancelUrl,
        @NotBlank String returnUrl




) {
}
