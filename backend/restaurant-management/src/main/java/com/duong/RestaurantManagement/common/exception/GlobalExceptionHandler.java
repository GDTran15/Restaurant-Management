package com.duong.RestaurantManagement.common.exception;

import com.duong.RestaurantManagement.dining.exception.DiningSessionNotActiveException;
import com.duong.RestaurantManagement.food.exception.FoodAvailabilityException;
import com.duong.RestaurantManagement.food.exception.FoodCategoryNotEmptyException;
import com.duong.RestaurantManagement.invoice.exception.InvoiceHasBeenPaidException;
import com.duong.RestaurantManagement.menu.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.table.exception.InvalidQrCodeException;

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
    @ExceptionHandler({IllegalArgumentException.class, jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<ErrorApiResponse> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorApiResponse(400, ex.getMessage()));
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorApiResponse> conflict(Exception ex) {
        return ResponseEntity.status(409).body(new ErrorApiResponse(409, "Operation conflicts with existing records"));
    }
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorApiResponse> unauthorized(Exception ex) {
        return ResponseEntity.status(401).body(new ErrorApiResponse(401, "Invalid credentials"));
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorApiResponse> forbidden(Exception ex) {
        return ResponseEntity.status(403).body(new ErrorApiResponse(403, "Access denied"));
    }


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

    @ExceptionHandler(value = InvoiceHasBeenPaidException.class)
    public ResponseEntity<ErrorApiResponse> invoiceHasBeenPaid(InvoiceHasBeenPaidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
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
