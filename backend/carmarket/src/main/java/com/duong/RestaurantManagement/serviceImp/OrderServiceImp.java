package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.*;
import com.duong.RestaurantManagement.repo.FoodRepo;
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
    private final FoodRepo foodRepo;

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
    public void completeOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).
                orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        checkIfOrderIsAbleToUpdate(order,OrderStatus.COMPLETED);
        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepo.save(order);

    }

    @Override
    public void orderStartToProcess(Long orderId){
        Order order = orderRepo.findById(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Order not found")
                );
        checkIfOrderIsAbleToUpdate(order,OrderStatus.IN_PROGRESS);
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        orderRepo.save(order);
    }




    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
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
        List<Order> orders= orderRepo.findByDiningSession_DiningSessionId(diningSessionId);
        return orders.stream()
                .map((order -> {
                    return  new GetCustomerOrderDTO(
                            order.getOrderId(),
                            order.getOrderNumber(),
                            order.getCreatedAt(),
                            order.getOrderStatus(),
                            order.getOrderPrice(),
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


    private void checkIfOrderIsAbleToUpdate(Order order,OrderStatus updateStatus) {
        if (order.getOrderStatus() == OrderStatus.PENDING && (updateStatus == OrderStatus.IN_PROGRESS || updateStatus == OrderStatus.CANCELLED))  return;

        if  (order.getOrderStatus() == OrderStatus.IN_PROGRESS &&  updateStatus == OrderStatus.COMPLETED) return;

        String message = STR."Cannot change from\{order.getOrderStatus()} to \{updateStatus}";

        throw new InvalidOrderStateException(message);
    }

    private String generateOrderNumber() {
        return STR."ORD-\{LocalDate.now().toString().replace("-", "")}-\{UUID.randomUUID().toString().substring(0, 6)}";
    }
}
