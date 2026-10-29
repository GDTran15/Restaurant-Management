package com.duong.RestaurantManagement.order.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.order.dto.response.*;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.order.model.OrderItem;
import com.duong.RestaurantManagement.order.model.OrderStatus;

import com.duong.RestaurantManagement.order.dto.request.AddOrderDTO;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.order.mapper.OrderMapper;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.order.repository.OrderRepo;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepo orderRepo;
    private final DiningSessionService diningSessionService;
    private final OrderItemService orderItemService;
    private final FoodRepo foodRepo;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void addOrder(AddOrderDTO addOrderDTO) {

       DiningSession diningSession = diningSessionService.validateDiningSessionActiveStatus(addOrderDTO.diningSessionId());

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .orderNumber(generateOrderNumber())
                .orderStatus(OrderStatus.PENDING)
                .orderPrice(BigDecimal.ZERO)
                .diningSession(diningSession)
                .build();

        orderRepo.save(order);

        BigDecimal total =  orderItemService.addListOfOrderItemAndGetTotalPrice(addOrderDTO.orderItemDTOS(),order);

        order.setOrderPrice(total);

        orderRepo.save(order);
        applicationEventPublisher.publishEvent(new CreateNewOrderEvent(order.getOrderId(), addOrderDTO.diningSessionId()));

    }

    @Override
    @Transactional
    public void completeOrder(Order order) {

        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepo.save(order);

    }

    @Override
    @Transactional
    public void processOrder(Order order){
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderRepo.save(order);
    }

 @Override
 @Transactional
    public void cancelOrder(Order order) {

        order.setOrderStatus(OrderStatus.CANCELLED);
        for (OrderItem orderItem : order.getOrderItems()) {
            int update = foodRepo.updateFoodQuantityAfterOrderCancel(orderItem.getFood().getFoodId(),orderItem.getQuantity());
            if (update == 0) {
                throw new ResourceNotFoundException("Food not found with id: " );
            }
        }


    }
    private void checkIfOrderIsAbleToUpdate(Order order,OrderStatus updateStatus) {
        if (order.getOrderStatus() == OrderStatus.PENDING && (updateStatus == OrderStatus.IN_PROGRESS || updateStatus == OrderStatus.CANCELLED))  return;

        if  (order.getOrderStatus() == OrderStatus.IN_PROGRESS &&  updateStatus == OrderStatus.COMPLETED) return;

        throw new InvalidOrderStateException("Cannot change from " + order.getOrderStatus() + " to " + updateStatus);
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepo.findLockedById(orderId)
                               .orElseThrow(
                                       () -> new ResourceNotFoundException("Order not found")
                               );
        checkIfOrderIsAbleToUpdate(order,orderStatus);
        String oldStatus = order.getOrderStatus().name();
        switch (orderStatus){
           case COMPLETED -> completeOrder(order);
           case IN_PROGRESS -> processOrder(order);
           case CANCELLED -> cancelOrder(order);
       }
       applicationEventPublisher.publishEvent(new OrderStatusEvent(orderId,oldStatus,order.getOrderStatus().name(),
               order.getDiningSession().getDiningSessionId()));

    }


    @Override
    public List<GetCustomerOrderDTO> getDiningSessionOrder(Long diningSessionId) {
        List<Order> orders= getOrderFromDiningSession(diningSessionId);
        return orders.stream()
                .map(OrderMapper::toCustomerDTO).toList();
    }

    @Override
    public List<GetOrderForAdminDTO> getActiveDiningSessionOrder() {
        List<Order> orders = orderRepo.findByActiveDiningSession(DiningStatus.ACTIVE);

        return orders.stream()
                .map(OrderMapper::orderToGetOrderForAdminDTO) .toList();
    }

    @Override
    public List<GetOrderForAdminDTO> getOrderByStatus(OrderStatus orderStatus) {
        List<Order> orders = orderRepo.findByActiveDiningSessionAndOrderStatus(DiningStatus.ACTIVE,orderStatus);

        return orders.stream()
                .map(OrderMapper::orderToGetOrderForAdminDTO).toList();
    }

    @Override
    public List<Order> getOrderFromDiningSession(Long diningSessionId) {
        return orderRepo.findByDiningSession_DiningSessionId(diningSessionId);
    }

    @Override
    public boolean hasActiveOrderByDiningSession(Long diningSessionId) {
        return orderRepo.existsByDiningSession_DiningSessionIdAndOrderStatusIn(
                diningSessionId,
                List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS)
        );
    }




    @Override
    public List<GetOrderNumberForEachStatus> getNumberOfOrderForEachStatus() {
        Map<OrderStatus,Long> map = new EnumMap<>(OrderStatus.class);
        for (OrderStatus orderStatus : OrderStatus.values()) {
            map.put(orderStatus, 0L);
        }
        List<GetOrderNumberForEachStatus> firstVersion = orderRepo.getOrderCountByOrderStatus();

        for (GetOrderNumberForEachStatus getOrderNumberForEachStatus : firstVersion) {
            map.put(getOrderNumberForEachStatus.orderStatus(), getOrderNumberForEachStatus.count());
        }
        List<GetOrderNumberForEachStatus> result = new ArrayList<>();

        for (OrderStatus orderStatus : OrderStatus.values()) {
            result.add(new GetOrderNumberForEachStatus(orderStatus, map.get(orderStatus)));
        }

        return result;
    }




    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID();
    }
}
