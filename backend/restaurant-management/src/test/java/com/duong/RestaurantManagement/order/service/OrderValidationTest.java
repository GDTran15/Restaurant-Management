package com.duong.RestaurantManagement.order.service;

import com.duong.RestaurantManagement.order.dto.request.*;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderValidationTest {
    @Test
    void invalidOrdersFailBeforeDatabaseAccess() {
        var service = new OrderServiceImp(null,null,null,null);
        assertThrows(IllegalArgumentException.class, () -> service.addOrder(new AddOrderDTO(1L,List.of())));
        assertThrows(IllegalArgumentException.class, () -> service.addOrder(new AddOrderDTO(1L,List.of(new OrderItemDTO(1,-2)))));
        try (var factory=Validation.buildDefaultValidatorFactory()) {
            var validator=factory.getValidator();
            assertFalse(validator.validate(new AddOrderDTO(null,List.of(new OrderItemDTO(1,0)))).isEmpty());
            assertTrue(validator.validate(new AddOrderDTO(1L,List.of(new OrderItemDTO(1,2)))).isEmpty());
        }
    }
}
