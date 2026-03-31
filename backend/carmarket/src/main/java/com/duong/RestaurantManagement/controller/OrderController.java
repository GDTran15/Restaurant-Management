package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderForAdminDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderNumberForEachStatus;
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

    @PatchMapping("/{orderId}/cancelled")
    ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();

    }
    @GetMapping("")
    public ResponseEntity<List<GetCustomerOrderDTO>> getOrders(@RequestParam Long diningSessionId) {
        return ResponseEntity.ok(orderService.getDiningSessionOrder(diningSessionId));
    }



    @GetMapping("/active-session")
    public ResponseEntity<List<GetOrderForAdminDTO>> getActiveOrder() {
        return ResponseEntity.ok(orderService.getActiveDiningSessionOrder());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<GetOrderForAdminDTO>> getPendingOrder() {
        return ResponseEntity.ok(orderService.getOrderByStatus(OrderStatus.PENDING));
    }
    @GetMapping("/in-progress")
    public ResponseEntity<List<GetOrderForAdminDTO>> getInProgressOrder() {
        return ResponseEntity.ok(orderService.getOrderByStatus(OrderStatus.IN_PROGRESS));
    }
    @GetMapping("/completed")
    public ResponseEntity<List<GetOrderForAdminDTO>> getCompletedOrder() {
        return ResponseEntity.ok(orderService.getOrderByStatus(OrderStatus.COMPLETED));
    }
    @GetMapping("/cancelled")
    public ResponseEntity<List<GetOrderForAdminDTO>> getCancelledOrder() {
        return ResponseEntity.ok(orderService.getOrderByStatus(OrderStatus.CANCELLED));
    }

    @GetMapping("/count/session")
    public ResponseEntity<List<GetOrderNumberForEachStatus>> getOrderSummary() {
        return ResponseEntity.ok(orderService.getNumberOfOrderForEachStatus());
    }





}