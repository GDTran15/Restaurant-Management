package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.model.OrderStatus;
import com.duong.RestaurantManagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    ResponseEntity<Void> addOrders(@RequestBody AddOrderDTO addOrderDTO) {
        orderService.addOrder(addOrderDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/complete")
    ResponseEntity<Void> UpdateOrderStatus(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{orderId}/processing")
    ResponseEntity<Void> UpdateOrderProcessing(@PathVariable Long orderId) {
        orderService.orderStartToProcess(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/canceled")
    ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("")
    public ResponseEntity<List<GetCustomerOrderDTO>> getOrders(@RequestParam Long diningSessionId) {
        return ResponseEntity.ok(orderService.getDiningSessionOrder(diningSessionId));
    }


}