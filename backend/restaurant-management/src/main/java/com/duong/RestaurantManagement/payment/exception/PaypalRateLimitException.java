package com.duong.RestaurantManagement.payment.exception;

public class PaypalRateLimitException extends PaymentProviderException {
    public PaypalRateLimitException(String message) {
        super(message);
    }
}
