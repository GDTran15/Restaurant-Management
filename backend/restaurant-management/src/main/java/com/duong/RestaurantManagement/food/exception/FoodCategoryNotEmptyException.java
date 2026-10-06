package com.duong.RestaurantManagement.food.exception;

public class FoodCategoryNotEmptyException extends RuntimeException {
    public FoodCategoryNotEmptyException(String message) {
        super(message);
    }
}
