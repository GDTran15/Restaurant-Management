package com.duong.RestaurantManagement.payment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.duong.RestaurantManagement.payment.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.duong.RestaurantManagement.payment.dto.request.CreateOrderPaypalRequest;
import com.duong.RestaurantManagement.payment.dto.response.CreateOrderPaypalResponse;
import com.duong.RestaurantManagement.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/payments/paypal/orders", "/api/payments/paypal/orders"})
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public CreateOrderPaypalResponse createPaypalOrder(@Valid @RequestBody CreateOrderPaypalRequest request) {
        return paymentService.createPaypalOrder(request.invoiceId());
    }

    @PostMapping("/{orderId}/capture")
    public PaymentResponse capturePaypalOrder(@PathVariable String orderId) {
        return paymentService.capturePaypalOrder(orderId);
    }
}
