package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.dining_session.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.exception.DiningSessionNotActiveException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.DiningSession;
import com.duong.RestaurantManagement.model.DiningStatus;
import com.duong.RestaurantManagement.repo.DiningSessionRepo;
import com.duong.RestaurantManagement.repo.RestaurantTableRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiningSessionServiceImp implements DiningSessionService {

    private final DiningSessionRepo diningSessionRepo;
    private final RestaurantTableService restaurantTableService;
    private final RestaurantTableRepo restaurantTableRepo;

    @Override
    public boolean checkIfAnyDinningSessionActive() {
        return diningSessionRepo.existsByDiningStatus(DiningStatus.ACTIVE);
    }

    @Override
    public GetDiningSessionDTO getDiningSession(String tableQrToken) {
        restaurantTableService.validateTableToken(tableQrToken);
        Optional<DiningSession> diningSessionExist = diningSessionRepo.findByDiningStatusAndRestaurantTable_TableQrCodeValue(DiningStatus.ACTIVE, tableQrToken);
        if (diningSessionExist.isPresent()) {

            return new GetDiningSessionDTO( diningSessionExist.get().getDiningSessionId());
        }

            DiningSession diningSession = DiningSession
                    .builder()
                    .startAt(LocalDateTime.now())
                    .diningStatus(DiningStatus.ACTIVE)
                    .restaurantTable(restaurantTableRepo.findByTableQrCodeValue(tableQrToken)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Restaurant table not found")
                            ))
                    .build();
            diningSessionRepo.save(diningSession);
            return new GetDiningSessionDTO(diningSession.getDiningSessionId());

    }

    @Override
    public DiningSession validateDiningSessionActiveStatus(Long diningSessionId) {
        return diningSessionRepo.findByDiningStatusAndDiningSessionId(DiningStatus.ACTIVE,diningSessionId)
                .orElseThrow(
                        () -> new DiningSessionNotActiveException("Dining Session Not Active")
                );

    }

}
