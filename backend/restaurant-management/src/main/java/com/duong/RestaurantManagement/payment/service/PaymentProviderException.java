package com.duong.RestaurantManagement.payment.service;
@org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.BAD_GATEWAY)
public class PaymentProviderException extends RuntimeException {
    public PaymentProviderException(String message, Throwable cause) { super(message, cause); }
}
