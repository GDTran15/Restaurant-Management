package com.duong.RestaurantManagement.payment.service;


import com.duong.RestaurantManagement.payment.dto.request.CashPaymentRequest;
import com.duong.RestaurantManagement.payment.dto.request.CreateOrderPaypalRequest;
import com.duong.RestaurantManagement.payment.model.Payment;
import com.paypal.sdk.models.Order;

public interface PaymentService {


    Order createOrder(CreateOrderPaypalRequest request, String method, String intent, String description);

     Order captureOrder(String orderId);

     Payment createCashPayment(CashPaymentRequest request);
}
