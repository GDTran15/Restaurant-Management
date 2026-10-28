package com.duong.RestaurantManagement.payment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/payments/paypal/orders", "/api/payments/paypal/orders"})
public class PaymentController {
    // Checkout and capture routes are disabled until PaymentService implements
    // invoice ownership, payment persistence and capture reconciliation.
}
