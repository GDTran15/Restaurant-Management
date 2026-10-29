package com.duong.RestaurantManagement.common.exception;

import com.duong.RestaurantManagement.dining.exception.DiningSessionNotActiveException;
import com.duong.RestaurantManagement.food.exception.FoodAvailabilityException;
import com.duong.RestaurantManagement.food.exception.FoodCategoryNotEmptyException;
import com.duong.RestaurantManagement.invoice.exception.InvoiceHasBeenPaidException;
import com.duong.RestaurantManagement.menu.exception.MenuModificationNotAllowedException;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.payment.exception.InvalidPaypalCredentialException;
import com.duong.RestaurantManagement.payment.exception.PaypalRateLimitException;
import com.duong.RestaurantManagement.payment.service.PaymentProviderException;
import com.duong.RestaurantManagement.table.exception.InvalidQrCodeException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPaypalCredentialException.class)
    public ResponseEntity<ProblemDetail> handlePaypalInvalidCredentialException(
            InvalidPaypalCredentialException ex
    ) {


        ProblemDetail error = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage()
        );


        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }
    @ExceptionHandler(PaymentProviderException.class)
    public ResponseEntity<ProblemDetail> handlePaymentProvider(
            PaymentProviderException ex
    ) {
        ProblemDetail error = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }


    @ExceptionHandler(PaypalRateLimitException.class)
    public ResponseEntity<ProblemDetail> handlePaypalRateLimitException(
            PaypalRateLimitException ex
    ) {

        ProblemDetail error = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage()
        );


        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }




    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    public ProblemDetail badRequest(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail conflict(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Operation conflicts with existing records");
    }
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ProblemDetail unauthorized(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail forbidden(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
    }


    @ExceptionHandler(value = DuplicateResourceException.class)
    public ProblemDetail resourceAlreadyExist(DuplicateResourceException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Resource already exists");
        problem.setProperty("errors", e.getErrors());
        return problem;
    }

    @ExceptionHandler(value = FoodAvailabilityException.class)
    public ProblemDetail foodNotAvailability(FoodAvailabilityException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ProblemDetail resourceNotFound(ResourceNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = FoodCategoryNotEmptyException.class)
    public ProblemDetail foodCategoryNotEmpty(FoodCategoryNotEmptyException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
    @ExceptionHandler(value = MenuModificationNotAllowedException.class)
    public ProblemDetail menuCannotModified(MenuModificationNotAllowedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(value = InvalidQrCodeException.class)
    public ProblemDetail qrCodeInvalid(InvalidQrCodeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = DiningSessionNotActiveException.class)
    public ProblemDetail diningSessionIsNotActive(DiningSessionNotActiveException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
    @ExceptionHandler(value = InvalidOrderStateException.class)
    public ProblemDetail invalidOrderState(InvalidOrderStateException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(value = InvoiceHasBeenPaidException.class)
    public ProblemDetail invoiceHasBeenPaid(InvoiceHasBeenPaidException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail expiredJwtException(ExpiredJwtException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
    }

}

