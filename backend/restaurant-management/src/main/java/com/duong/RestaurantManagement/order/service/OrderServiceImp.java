package com.duong.RestaurantManagement.order.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.order.model.OrderItem;
import com.duong.RestaurantManagement.order.model.OrderStatus;

import com.duong.RestaurantManagement.order.dto.request.AddOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderForAdminDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderNumberForEachStatus;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.order.mapper.OrderMapper;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.order.repository.OrderRepo;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import com.duong.RestaurantManagement.order.service.OrderItemService;
import com.duong.RestaurantManagement.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class OrderServiceImp implements OrderService {

     private final OrderRepo orderRepo;
     private final DiningSessionService diningSessionService;
    private final OrderItemService orderItemService;
    private final FoodRepo foodRepo;

    @Override
    @Transactional
    public void addOrder(AddOrderDTO addOrderDTO) {
       if (addOrderDTO == null || addOrderDTO.diningSessionId() == null || addOrderDTO.diningSessionId() <= 0
               || addOrderDTO.orderItemDTOS() == null || addOrderDTO.orderItemDTOS().isEmpty()
               || addOrderDTO.orderItemDTOS().stream().anyMatch(i -> i == null || i.foodId() <= 0 || i.quantity() <= 0)) {
           throw new IllegalArgumentException("A session and positive order quantities are required");
       }
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


    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepo.findLockedById(orderId).
                orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        checkIfOrderIsAbleToUpdate(order,OrderStatus.COMPLETED);
        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepo.save(order);

    }

    @Override
    @Transactional
    public void orderStartToProcess(Long orderId){
        Order order = orderRepo.findLockedById(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        checkIfOrderIsAbleToUpdate(order,OrderStatus.IN_PROGRESS);
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderRepo.save(order);
    }

 @Override
 @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepo.findLockedById(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        checkIfOrderIsAbleToUpdate(order,OrderStatus.CANCELLED);

        order.setOrderStatus(OrderStatus.CANCELLED);

        for (OrderItem orderItem : order.getOrderItems()) {
            int update = foodRepo.updateFoodQuantityAfterOrderCancel(orderItem.getFood().getFoodId(),orderItem.getQuantity());
            if (update == 0) {
                throw new ResourceNotFoundException("Food not found with id: " );
            }
        }


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
                .map(OrderMapper::orderToGetOrderForAdminDTO) .toList();
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


    private void checkIfOrderIsAbleToUpdate(Order order,OrderStatus updateStatus) {
        if (order.getOrderStatus() == OrderStatus.PENDING && (updateStatus == OrderStatus.IN_PROGRESS || updateStatus == OrderStatus.CANCELLED))  return;

        if  (order.getOrderStatus() == OrderStatus.IN_PROGRESS &&  updateStatus == OrderStatus.COMPLETED) return;

        String message = "Cannot change from " + order.getOrderStatus() + " to " + updateStatus;

        throw new InvalidOrderStateException(message);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID();
    }
}
