package com.duong.RestaurantManagement.dining.dto.response;

public record GetDiningSessionDTO(
        Long dinningSessionId,
        String accessToken
) {
}
