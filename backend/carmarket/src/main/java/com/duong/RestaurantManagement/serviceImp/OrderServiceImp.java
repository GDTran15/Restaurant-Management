package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderItemDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
                .orderNumber(generateOrderNumber())
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

    @Override
    public List<GetCustomerOrderDTO> getDiningSessionOrder(Long diningSessionId) {
        List<Order> orders= orderRepo.findByDiningSession_DiningSessionId(diningSessionId);
        return orders.stream()
                .map((order -> {
                    return  new GetCustomerOrderDTO(
                            order.getOrderNumber(),
                            order.getCreatedAt(),
                            order.getOrderStatus(),
                            order.getOrderItems()
                                    .stream()
                                    .map((orderItem ->
                                            {
                                                return new GetOrderItemDTO(
                                                        orderItem.getFood().getFoodName(),
                                                        orderItem.getQuantity(),
                                                        orderItem.getTotalPrice()
                                                );
                                            })
                                    ).toList()
                    );
                })).toList();
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

    private String generateOrderNumber() {
        return STR."ORD-\{LocalDate.now().toString().replace("-", "")}-\{UUID.randomUUID().toString().substring(0, 6)}";
    }
}
