package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.request.OrderItemDTO;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.*;
import com.duong.RestaurantManagement.repo.DiningSessionRepo;
import com.duong.RestaurantManagement.repo.FoodRepo;
import com.duong.RestaurantManagement.repo.OrderItemRepo;
import com.duong.RestaurantManagement.repo.OrderRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.OrderItemService;
import com.duong.RestaurantManagement.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

     private final OrderRepo orderRepo;
     private final DiningSessionRepo diningSessionRepo;
     private final OrderItemRepo orderItemRepo;
    private final FoodRepo foodRepo;
    private final DiningSessionService diningSessionService;
    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public void addOrder(AddOrderDTO addOrderDTO) {
       DiningSession diningSession = diningSessionService.validateDiningSessionActiveStatus(addOrderDTO.diningSessionId());
        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .orderStatus(OrderStatus.IN_PROGRESS)
                .orderPrice(0)
                .diningSession(diningSession)
                .build();

        orderRepo.save(order);

        double total =  orderItemService.addListOfOrderItemAndGetTotalPrice(addOrderDTO.orderItemDTOS(),order);

        order.setOrderPrice(total);
        orderRepo.save(order);


    }
}
