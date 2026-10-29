package com.duong.RestaurantManagement.dining.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.auth.service.JwtService;
import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.menu.service.MenuStateLock;
import com.duong.RestaurantManagement.order.mapper.OrderMapper;
import com.duong.RestaurantManagement.order.model.OrderItem;
import com.duong.RestaurantManagement.order.model.OrderStatus;
import com.duong.RestaurantManagement.table.model.RestaurantTable;

import com.duong.RestaurantManagement.dining.dto.response.GetDiningSessionDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.dining.exception.DiningSessionNotActiveException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.dining.repository.DiningSessionRepo;
import com.duong.RestaurantManagement.order.repository.OrderItemRepo;
import com.duong.RestaurantManagement.order.repository.OrderRepo;
import com.duong.RestaurantManagement.table.repository.RestaurantTableRepo;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import com.duong.RestaurantManagement.table.service.RestaurantTableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class DiningSessionServiceImp implements DiningSessionService {
    private final MenuStateLock menuStateLock;

    private final DiningSessionRepo diningSessionRepo;
    private final JwtService jwtService;
    private final RestaurantTableRepo restaurantTableRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;

    @Override
    public boolean checkIfAnyDinningSessionActive() {
        return diningSessionRepo.existsByDiningStatus(DiningStatus.ACTIVE)
                || diningSessionRepo.existsByDiningStatus(DiningStatus.WAITING_FOR_PAYMENT);
    }

    @Override
    @Transactional
    public GetDiningSessionDTO getDiningSession(String tableQrToken) {
        menuStateLock.acquire();

        RestaurantTable restaurantTable = restaurantTableRepo.findByTableQrCodeValue(tableQrToken)
                .orElseThrow(
                        () -> new ResourceNotFoundException("QrCode not found for table ")
                );

        Optional<DiningSession> diningSessionExist = diningSessionRepo.
                findFirstByRestaurantTable_TableQrCodeValueAndDiningStatusNot(tableQrToken, DiningStatus.COMPLETED);

        if (diningSessionExist.isPresent()) {
            return new GetDiningSessionDTO(
                    diningSessionExist.get().getDiningSessionId(),
                    jwtService.generateDiningToken(diningSessionExist.get().getDiningSessionId())
            );
        }

            DiningSession diningSession = DiningSession
                    .builder()
                    .startAt(LocalDateTime.now())
                    .diningStatus(DiningStatus.ACTIVE)
                    .restaurantTable(restaurantTable)
                    .build();

            diningSessionRepo.save(diningSession);

            restaurantTable.setRestaurantTableStatus(false);

            return new GetDiningSessionDTO(diningSession.getDiningSessionId(), jwtService.generateDiningToken(diningSession.getDiningSessionId()));

    }

    @Override
    public DiningSession validateDiningSessionActiveStatus(Long diningSessionId) {
        DiningSession session = diningSessionRepo.findLockedById(diningSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Dining session not found"));
        if (session.getDiningStatus() != DiningStatus.ACTIVE) throw new DiningSessionNotActiveException("Ordering is closed for this session");
        return session;

    }

    @Override
    @Transactional
    public void deactiveDinningSession(Long diningSessionId) {
       DiningSession diningSession =  diningSessionRepo.findLockedById(diningSessionId).orElseThrow(() ->
               new ResourceNotFoundException("Dining session not found"));
       diningSession.setDiningStatus(DiningStatus.COMPLETED);

       diningSession.setEndAt(LocalDateTime.now());

       diningSession.getRestaurantTable().setRestaurantTableStatus(true);
       diningSessionRepo.save(diningSession);
    }

    @Override
    public BigDecimal getDiningSessionTotalOrderPrice(Long diningSessionId) {
        return orderRepo.getTotalOrderPriceByDiningSessionIdAndStatus(diningSessionId,OrderStatus.COMPLETED);
    }

    @Override
    public List<GetOrderItemDTO> getDiningSessionOrderItems(Long diningSessionId) {
        return orderItemRepo.findByOrder_DiningSession_DiningSessionIdAndOrder_OrderStatus(
                diningSessionId, OrderStatus.COMPLETED).stream()
                            .map(OrderMapper::toOrderItemDTO)
                            .toList();
    }



}
