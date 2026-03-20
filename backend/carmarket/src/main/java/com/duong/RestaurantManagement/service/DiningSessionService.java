package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.dining_session.response.GetDiningSessionDTO;

public interface DiningSessionService {

    boolean checkIfAnyDinningSessionActive();

    GetDiningSessionDTO getDiningSession(String tableQrToken);
}
