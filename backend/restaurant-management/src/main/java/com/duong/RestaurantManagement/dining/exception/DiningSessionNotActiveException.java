package com.duong.RestaurantManagement.dining.exception;

public class DiningSessionNotActiveException extends RuntimeException {
    public DiningSessionNotActiveException(String message) {
        super(message);
    }
}
