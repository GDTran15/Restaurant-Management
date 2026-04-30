package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.order.request.OrderItemDTO;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.Food;
import com.duong.RestaurantManagement.model.Order;
import com.duong.RestaurantManagement.model.OrderItem;
import com.duong.RestaurantManagement.repo.FoodRepo;
import com.duong.RestaurantManagement.repo.OrderItemRepo;
import com.duong.RestaurantManagement.service.OrderItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImp implements OrderItemService {

    private final FoodRepo foodRepo;
    private final OrderItemRepo orderItemRepo;

    @Override
    @Transactional
    public double addListOfOrderItemAndGetTotalPrice(List<OrderItemDTO> orderItemDTOS, Order order) {
        double totalPrice = order.getOrderPrice();
        for (OrderItemDTO orderItemDTO : orderItemDTOS){
            Food food = foodRepo.findById(orderItemDTO.foodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
            int decrease = foodRepo.decreaseFoodQuantity( food.getFoodId(), orderItemDTO.quantity());
            if (decrease == 0) {
                throw new RuntimeException("Not enough stock or food not found");
            }
            OrderItem orderItem = OrderItem.builder()
                    .quantity(orderItemDTO.quantity())
                    .order(order)
                    .food(food)
                    .totalPrice(food.getPrice()* orderItemDTO.quantity())
                    .build();
            orderItemRepo.save(orderItem);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
