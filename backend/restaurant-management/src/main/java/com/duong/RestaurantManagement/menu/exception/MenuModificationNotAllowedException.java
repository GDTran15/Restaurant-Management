package com.duong.RestaurantManagement.menu.exception;

public class MenuModificationNotAllowedException extends RuntimeException {
    public MenuModificationNotAllowedException(String message) {
        super(message);
    }
}
