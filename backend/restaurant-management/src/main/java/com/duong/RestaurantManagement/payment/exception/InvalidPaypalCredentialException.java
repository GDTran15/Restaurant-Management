package com.duong.RestaurantManagement.payment.exception;

import com.duong.RestaurantManagement.payment.service.PaymentProviderException;

public class InvalidPaypalCredentialException extends PaymentProviderException {
    public InvalidPaypalCredentialException(String message) {
        super(message);
    }
}
