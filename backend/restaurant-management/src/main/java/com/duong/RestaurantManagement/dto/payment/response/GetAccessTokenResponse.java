package com.duong.RestaurantManagement.dto.payment.response;

public record GetAccessTokenResponse (
        String scope,
        String access_token,
        String token_type,
        String app_id,
        Integer expires_in,
        String nonce
){
}
