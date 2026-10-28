package com.duong.RestaurantManagement.payment.exception;

import com.duong.RestaurantManagement.payment.service.PaymentProviderException;

public class InvalidPaypalRequestException extends PaymentProviderException {
    public InvalidPaypalRequestException(String message) {
        super(message);
    }
}
