package com.duong.RestaurantManagement.order.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.food.exception.FoodAvailabilityException;
import com.duong.RestaurantManagement.menu.repository.MenuItemRepo;
import com.duong.RestaurantManagement.order.dto.request.OrderItemDTO;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.food.model.Food;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.order.model.OrderItem;
import com.duong.RestaurantManagement.food.repository.FoodRepo;
import com.duong.RestaurantManagement.order.repository.OrderItemRepo;
import com.duong.RestaurantManagement.order.service.OrderItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImp implements OrderItemService {

    private final FoodRepo foodRepo;
    private final OrderItemRepo orderItemRepo;
    private final MenuItemRepo menuItemRepo;

    @Override
    @Transactional
    public BigDecimal addListOfOrderItemAndGetTotalPrice(List<OrderItemDTO> orderItemDTOS, Order order) {
        BigDecimal totalPrice = order.getOrderPrice();

        for (OrderItemDTO orderItemDTO : orderItemDTOS.stream().sorted(Comparator.comparingLong(OrderItemDTO::foodId)).toList()){

            Food food = foodRepo.findLockedById(orderItemDTO.foodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

            if (!food.isAvailable() || !menuItemRepo.existsByFood_FoodIdAndMenu_IsActivatedTrue(food.getFoodId())) {
                throw new FoodAvailabilityException("Food is unavailable or outside the active menu");
            }

            int decrease = foodRepo.decreaseFoodQuantity(food.getFoodId(), orderItemDTO.quantity());

            if (decrease == 0) {
                throw new FoodAvailabilityException("Not enough stock");
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
