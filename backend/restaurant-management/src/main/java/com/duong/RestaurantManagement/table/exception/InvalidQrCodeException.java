package com.duong.RestaurantManagement.table.exception;

public class InvalidQrCodeException extends RuntimeException {
    public InvalidQrCodeException(String message) {
        super(message);
    }
}
