package com.duong.RestaurantManagement.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String,String>> resourceAlreadyExist(DuplicateResourceException e) {
        return new ResponseEntity<>(e.getErrors(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = FoodAvailabilityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> foodNotAvailability(FoodAvailabilityException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
                );
    }
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorApiResponse> resourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage())
                );
    }

    @ExceptionHandler(value = FoodCategoryNotEmptyException.class)
    public ResponseEntity<ErrorApiResponse> foodCategoryNotEmpty(FoodCategoryNotEmptyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorApiResponse(HttpStatus.CONFLICT.value(), e.getMessage())
                );
    }
    @ExceptionHandler(value = MenuModificationNotAllowedException.class)
    public ResponseEntity<ErrorApiResponse> menuCannotModified(MenuModificationNotAllowedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorApiResponse(HttpStatus.CONFLICT.value(), e.getMessage())
                );
    }

    @ExceptionHandler(value = InvalidQrCodeException.class)
    public ResponseEntity<ErrorApiResponse> qrCodeInvalid(InvalidQrCodeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage())
                );
    }

    @ExceptionHandler(value = DiningSessionNotActiveException.class)
    public ResponseEntity<ErrorApiResponse> diningSessionIsNotActive(DiningSessionNotActiveException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorApiResponse(HttpStatus.CONFLICT.value(), e.getMessage())
                );
    }
    @ExceptionHandler(value = InvalidOrderStateException.class)
    public ResponseEntity<ErrorApiResponse> invalidOrderState(InvalidOrderStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorApiResponse(HttpStatus.CONFLICT.value(), e.getMessage())
                );
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorApiResponse> expiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorApiResponse(HttpStatus.UNAUTHORIZED.value(), "TOKEN_EXPIRED")
                );
    }

}
