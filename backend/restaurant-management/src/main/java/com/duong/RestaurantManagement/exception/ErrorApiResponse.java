package com.duong.RestaurantManagement.exception;

import lombok.Getter;

@Getter
public class ErrorApiResponse {
        private int status;
        private String message;

        public ErrorApiResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
}
