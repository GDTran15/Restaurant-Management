package com.duong.RestaurantManagement.dto.payment.request;

import java.math.BigDecimal;

public record CreateOrderPaypalRequest(
        Long invoiceId,
        String currencyCode,
        String cancelUrl,
        String returnUrl




) {
}
