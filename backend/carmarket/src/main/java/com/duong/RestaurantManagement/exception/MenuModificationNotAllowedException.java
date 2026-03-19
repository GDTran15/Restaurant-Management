package com.duong.RestaurantManagement.exception;

public class MenuModificationNotAllowedException extends RuntimeException {
    public MenuModificationNotAllowedException(String message) {
        super(message);
    }
}
