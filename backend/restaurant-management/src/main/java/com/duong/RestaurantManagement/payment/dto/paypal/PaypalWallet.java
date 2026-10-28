package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaypalWallet (
        @JsonProperty("experience_context")
        PaypalExperienceContext experienceContext
){
}
