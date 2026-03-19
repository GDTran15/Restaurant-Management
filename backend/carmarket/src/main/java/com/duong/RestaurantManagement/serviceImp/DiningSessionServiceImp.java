package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.model.DiningStatus;
import com.duong.RestaurantManagement.repo.DiningSessionRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiningSessionServiceImp implements DiningSessionService {

    private final DiningSessionRepo diningSessionRepo;

    @Override
    public boolean checkIfAnyDinningSessionActive() {
        return diningSessionRepo.existsByDiningStatus(DiningStatus.ACTIVE);
    }
}
