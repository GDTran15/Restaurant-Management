package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.dining_session.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.model.DiningSession;

public interface DiningSessionService {

    boolean checkIfAnyDinningSessionActive();

    GetDiningSessionDTO getDiningSession(String tableQrToken);

    DiningSession validateDiningSessionActiveStatus(Long aLong);

    void deactiveDinningSession(Long diningSessionId);
}
