package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PaypalPurchaseUnit (
        @JsonProperty("reference_id")
        String reference_id,
        PaypalAmount amount
){
}
