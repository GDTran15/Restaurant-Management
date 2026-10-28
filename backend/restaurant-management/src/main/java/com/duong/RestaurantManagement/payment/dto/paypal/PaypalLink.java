package com.duong.RestaurantManagement.payment.dto.paypal;

public record PaypalLink (
        String href,
        String rel,
        String method
){
}
