package com.duong.RestaurantManagement.order.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.order.dto.request.OrderItemDTO;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.order.model.OrderItem;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.order.repository.OrderItemRepo;
import com.duong.RestaurantManagement.order.service.OrderItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class OrderItemServiceImp implements OrderItemService {

    private final FoodRepo foodRepo;
    private final OrderItemRepo orderItemRepo;
    private final com.duong.RestaurantManagement.menu.repository.MenuItemRepo menuItemRepo;

    @Override
    @Transactional
    public BigDecimal addListOfOrderItemAndGetTotalPrice(List<OrderItemDTO> orderItemDTOS, Order order) {
        BigDecimal totalPrice = order.getOrderPrice();
        if (orderItemDTOS == null || orderItemDTOS.isEmpty()) throw new IllegalArgumentException("Order cannot be empty");
        for (OrderItemDTO orderItemDTO : orderItemDTOS.stream().sorted(java.util.Comparator.comparingLong(OrderItemDTO::foodId)).toList()){
            if (orderItemDTO.foodId() <= 0 || orderItemDTO.quantity() <= 0) throw new IllegalArgumentException("Invalid food or quantity");
            Food food = foodRepo.findLockedById(orderItemDTO.foodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
            if (!food.isAvailable() || !menuItemRepo.existsByFood_FoodIdAndMenu_IsActivatedTrue(food.getFoodId())) {
                throw new com.duong.RestaurantManagement.food.exception.FoodAvailabilityException("Food is unavailable or outside the active menu");
            }
            int decrease = foodRepo.decreaseFoodQuantity( food.getFoodId(), orderItemDTO.quantity());
            if (decrease == 0) {
                throw new com.duong.RestaurantManagement.food.exception.FoodAvailabilityException("Not enough stock");
            }
            OrderItem orderItem = OrderItem.builder()
                    .quantity(orderItemDTO.quantity())
                    .order(order)
                    .food(food)
                    .unitPrice(food.getPrice())
                    .totalPrice(food.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.quantity())))
                    .build();
            orderItemRepo.save(orderItem);
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }
        return totalPrice;
    }
}
