package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.payment.request.CreateOrderPaypalRequest;
import com.paypal.sdk.models.Order;

public interface PaymentService {


    Order createOrder(CreateOrderPaypalRequest request, String method, String intent, String description);

     Order captureOrder(String orderId);
}
