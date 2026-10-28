package com.duong.RestaurantManagement.payment.exception;

import com.duong.RestaurantManagement.payment.service.PaymentProviderException;

public class PaypalRateLimitException extends PaymentProviderException {
    public PaypalRateLimitException(String message) {
        super(message);
    }
}
