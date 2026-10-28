package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaypalAmount(
        @JsonProperty("currency_code")
        String currencyCode,

        String value
) {}