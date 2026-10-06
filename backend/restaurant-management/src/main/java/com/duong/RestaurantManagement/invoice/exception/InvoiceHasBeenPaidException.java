package com.duong.RestaurantManagement.invoice.exception;

public class InvoiceHasBeenPaidException extends RuntimeException {
    public InvoiceHasBeenPaidException(String message) {
        super(message);
    }
}
