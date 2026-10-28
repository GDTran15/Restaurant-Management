package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaypalPaymentSource(
        @JsonProperty("paypal")
        PaypalWallet paypalWallet
) {
}
