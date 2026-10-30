package com.duong.RestaurantManagement.payment.exception;

public class InvalidPaypalCredentialException extends PaymentProviderException {
    public InvalidPaypalCredentialException(String message) {
        super(message);
    }
}
