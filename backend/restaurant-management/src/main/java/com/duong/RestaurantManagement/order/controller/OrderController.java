package com.duong.RestaurantManagement.order.controller;

import com.duong.RestaurantManagement.auth.service.SessionAccess;
import com.duong.RestaurantManagement.order.dto.request.AddOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderForAdminDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderNumberForEachStatus;
import com.duong.RestaurantManagement.order.model.OrderStatus;
import com.duong.RestaurantManagement.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final SessionAccess sessionAccess;

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> addOrders(@Valid @RequestBody AddOrderDTO addOrderDTO) {
        sessionAccess.requireSession(addOrderDTO.diningSessionId());
        orderService.addOrder(addOrderDTO);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long orderId, @RequestParam OrderStatus orderStatus){
         orderService.updateStatus(orderId,orderStatus);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<List<GetCustomerOrderDTO>> getOrders(@RequestParam Long diningSessionId) {
        sessionAccess.requireSession(diningSessionId);
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