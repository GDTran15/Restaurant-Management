package com.duong.RestaurantManagement.exception;

public class FoodCategoryNotEmptyException extends RuntimeException {
    public FoodCategoryNotEmptyException(String message) {
        super(message);
    }
}
