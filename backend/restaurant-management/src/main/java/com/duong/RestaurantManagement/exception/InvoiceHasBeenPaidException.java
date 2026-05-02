package com.duong.RestaurantManagement.exception;

public class InvoiceHasBeenPaidException extends RuntimeException {
    public InvoiceHasBeenPaidException(String message) {
        super(message);
    }
}
