package com.duong.RestaurantManagement.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class DuplicateResourceException extends RuntimeException {

     private final Map<String,String> errors;



    public DuplicateResourceException(Map<String, String> errors) {
        this.errors = errors;
    }
    public Map<String,String> getErrors() {
        return errors;
    }
}
