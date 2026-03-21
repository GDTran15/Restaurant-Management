package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.model.OrderStatus;
import com.duong.RestaurantManagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    ResponseEntity<Void> addOrders(AddOrderDTO addOrderDTO) {
        orderService.addOrder(addOrderDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    ResponseEntity<Void> UpdateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId,orderStatus);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{orderId}/canceled")
    ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();

    }
}