package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaypalExperienceContext (
        @JsonProperty("return_url")
        String returnUrl,
        @JsonProperty("cancel_url")
        String cancelUrl,
        @JsonProperty("shipping_preference")
        String shippingPreference
) {
}
