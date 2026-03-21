package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.*;
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
     private final DiningSessionService diningSessionService;
    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public void addOrder(AddOrderDTO addOrderDTO) {
       DiningSession diningSession = diningSessionService.validateDiningSessionActiveStatus(addOrderDTO.diningSessionId());
        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .orderPrice(0)
                .diningSession(diningSession)
                .build();

        orderRepo.save(order);

        double total =  orderItemService.addListOfOrderItemAndGetTotalPrice(addOrderDTO.orderItemDTOS(),order);

        order.setOrderPrice(total);
        orderRepo.save(order);


    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepo.findById(orderId).
                orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        order.setOrderStatus(orderStatus);
        orderRepo.save(order);

    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        checkIfOrderIsAbleToCancel(order);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }


    private void checkIfOrderIsAbleToCancel(Order order) {

        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new InvalidOrderStateException("Order is already completed");
        } else if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
            throw new InvalidOrderStateException("Order is already in progress");
        } else if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled");
        }
    }
}
