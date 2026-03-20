package com.duong.RestaurantManagement.exception;

public class DiningSessionNotActiveException extends RuntimeException {
    public DiningSessionNotActiveException(String message) {
        super(message);
    }
}
