package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaypalTokenResponse(
        String scope,
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("app_id")
        String appId,
        @JsonProperty("expires_in")
        Integer expiresIn,
        String nonce
) {
}
