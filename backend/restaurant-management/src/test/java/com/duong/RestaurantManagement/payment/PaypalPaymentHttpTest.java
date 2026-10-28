package com.duong.RestaurantManagement.payment;

import com.duong.RestaurantManagement.payment.controller.PaymentController;
import com.duong.RestaurantManagement.common.exception.GlobalExceptionHandler;
import com.duong.RestaurantManagement.payment.service.PaymentProviderException;
import com.duong.RestaurantManagement.payment.exception.PaypalRateLimitException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaypalPaymentHttpTest {
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new PaymentController(), new FailureController())
            .setControllerAdvice(new GlobalExceptionHandler()).build();

    @Test
    void unfinishedPaymentRoutesAreUnavailable() throws Exception {
        for (String prefix : new String[]{"/payments/paypal/orders", "/api/payments/paypal/orders"}) {
            mvc.perform(post(prefix).contentType("application/json").content("{\"invoiceId\":1}"))
                    .andExpect(status().isNotFound());
            mvc.perform(post(prefix + "/ORDER-1/capture")).andExpect(status().isNotFound());
        }
    }

    @Test
    void providerFailuresRenderProblemDetails() throws Exception {
        mvc.perform(get("/test/paypal/failure")).andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("PayPal order request failed"));
        mvc.perform(get("/test/paypal/rate-limit")).andExpect(status().isServiceUnavailable());
    }

    @RestController
    static class FailureController {
        @GetMapping("/test/paypal/failure")
        void failure() { throw new PaymentProviderException("PayPal order request failed"); }

        @GetMapping("/test/paypal/rate-limit")
        void rateLimit() { throw new PaypalRateLimitException("Retry later"); }
    }
}
