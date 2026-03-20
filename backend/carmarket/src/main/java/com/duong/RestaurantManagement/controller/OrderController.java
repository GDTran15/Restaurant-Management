package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
