package com.duong.RestaurantManagement.payment.exception;

public class InvalidPaypalRequestException extends PaymentProviderException {
    public InvalidPaypalRequestException(String message) {
        super(message);
    }
}
