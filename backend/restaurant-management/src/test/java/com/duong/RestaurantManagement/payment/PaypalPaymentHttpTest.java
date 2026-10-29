package com.duong.RestaurantManagement.payment;

import com.duong.RestaurantManagement.payment.controller.PaymentController;
import com.duong.RestaurantManagement.common.exception.GlobalExceptionHandler;
import com.duong.RestaurantManagement.payment.service.PaymentProviderException;
import com.duong.RestaurantManagement.payment.service.PaymentService;
import com.duong.RestaurantManagement.payment.dto.response.CreateOrderPaypalResponse;
import com.duong.RestaurantManagement.payment.dto.response.PaymentResponse;
import com.duong.RestaurantManagement.payment.model.PaymentStatus;
import com.duong.RestaurantManagement.payment.exception.PaypalRateLimitException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

class PaypalPaymentHttpTest {
    private final PaymentService payments = mock(PaymentService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new PaymentController(payments), new FailureController())
            .setControllerAdvice(new GlobalExceptionHandler()).build();

    @Test
    void createsAndCapturesCheckoutForBothRoutePrefixes() throws Exception {
        when(payments.createPaypalOrder(1L)).thenReturn(new CreateOrderPaypalResponse(
                "ORDER-1", "https://www.sandbox.paypal.com/checkoutnow?token=ORDER-1", PaymentStatus.PENDING));
        when(payments.capturePaypalOrder("ORDER-1")).thenReturn(new PaymentResponse(
                2L, 1L, "ORDER-1", new java.math.BigDecimal("20.00"), "USD",
                com.duong.RestaurantManagement.payment.model.PaymentMethod.PAYPAL,
                PaymentStatus.COMPLETED, java.time.LocalDateTime.now()));
        for (String prefix : new String[]{"/payments/paypal/orders", "/api/payments/paypal/orders"}) {
            mvc.perform(post(prefix).contentType("application/json").content("{\"invoiceId\":1}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.providerOrderId").value("ORDER-1"))
                    .andExpect(jsonPath("$.approvalUrl").value("https://www.sandbox.paypal.com/checkoutnow?token=ORDER-1"))
                    .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
            mvc.perform(post(prefix + "/ORDER-1/capture")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"))
                    .andExpect(jsonPath("$.invoiceId").value(1));
        }
    }

    @Test
    void rejectsMissingOrInvalidInvoiceBeforeCallingService() throws Exception {
        for (String body : new String[]{"{}", "{\"invoiceId\":0}", "{\"invoiceId\":-1}"}) {
            mvc.perform(post("/payments/paypal/orders").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(payments);
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
