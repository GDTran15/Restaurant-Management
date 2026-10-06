package com.duong.RestaurantManagement.dining.service;

import java.math.BigDecimal;


import com.duong.RestaurantManagement.dining.dto.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.dining.model.DiningSession;

import java.util.List;

public interface DiningSessionService {

    boolean checkIfAnyDinningSessionActive();

    GetDiningSessionDTO getDiningSession(String tableQrToken);

    DiningSession validateDiningSessionActiveStatus(Long aLong);

    void deactiveDinningSession(Long diningSessionId);

    BigDecimal getDiningSessionTotalOrderPrice(Long diningSessionId);

    List<GetOrderItemDTO> getDiningSessionOrderItems(Long diningSessionId);
}
