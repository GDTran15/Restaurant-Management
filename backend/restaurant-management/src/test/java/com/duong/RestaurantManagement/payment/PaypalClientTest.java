package com.duong.RestaurantManagement.payment;

import com.duong.RestaurantManagement.RestClientConfig;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalTokenResponse;
import com.duong.RestaurantManagement.payment.exception.*;
import com.duong.RestaurantManagement.payment.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class PaypalClientTest {
    private final RestClient.Builder builder = RestClient.builder().baseUrl("https://api-m.sandbox.paypal.com");
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final PaypalAccessTokenProvider tokens = mock(PaypalAccessTokenProvider.class);
    private final PaypalOrderClient orders = new PaypalOrderClient(builder.build(), tokens);
    private final Invoice invoice = Invoice.builder().invoiceId(7L).totalPay(new BigDecimal("20.00")).build();

    PaypalClientTest() {
        ReflectionTestUtils.setField(orders, "frontendUrl", "https://restaurant.example");
        when(tokens.getValidAccessToken()).thenReturn("access-token");
    }

    @Test
    void serializesPaypalPaymentSourceAndReadsProviderResponse() {
        server.expect(requestTo("https://api-m.sandbox.paypal.com/v2/checkout/orders"))
                .andExpect(header("Authorization", "Bearer access-token"))
                .andExpect(jsonPath("$.payment_source.paypal.experience_context.shipping_preference").value("NO_SHIPPING"))
                .andExpect(jsonPath("$.payment_source.paypalWallet").doesNotExist())
                .andExpect(jsonPath("$.purchase_units[0].amount.value").value("20.00"))
                .andRespond(withSuccess("{\"id\":\"ORDER-1\",\"status\":\"CREATED\",\"links\":[]}", MediaType.APPLICATION_JSON));
        assertEquals("ORDER-1", orders.createPaypalOrder(invoice, "USD").id());
        server.verify();
    }

    @Test
    void createOrderSendsPersistedIdempotencyKeyAndSnapshot() {
        server.expect(requestTo("https://api-m.sandbox.paypal.com/v2/checkout/orders"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("PayPal-Request-Id", "stable-request-id"))
                .andExpect(jsonPath("$.purchase_units[0].reference_id").value("7"))
                .andExpect(jsonPath("$.purchase_units[0].amount.currency_code").value("USD"))
                .andExpect(jsonPath("$.purchase_units[0].amount.value").value("20.00"))
                .andRespond(withSuccess("{\"id\":\"ORDER-1\",\"status\":\"CREATED\",\"links\":[]}", MediaType.APPLICATION_JSON));
        assertEquals("ORDER-1", orders.createOrder(7L, new BigDecimal("20.00"), "USD", "stable-request-id").id());
        server.verify();
    }

    @Test
    void capturesOrderAndReadsInvoiceReferenceAndCaptureDetails() {
        server.expect(requestTo("https://api-m.sandbox.paypal.com/v2/checkout/orders/ORDER-1/capture"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer access-token"))
                .andExpect(header("Prefer", "return=representation"))
                .andExpect(header("PayPal-Request-Id", java.util.UUID.nameUUIDFromBytes(
                        "capture:ORDER-1".getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{}"))
                .andRespond(withSuccess("""
                        {
                          "id": "ORDER-1",
                          "status": "COMPLETED",
                          "payer": {"payer_id": "BUYER-1"},
                          "purchase_units": [{
                            "reference_id": "7",
                            "payments": {
                              "captures": [{
                                "id": "CAPTURE-1",
                                "status": "COMPLETED",
                                "amount": {"value": "20.00", "currency_code": "USD"},
                                "final_capture": true
                              }]
                            }
                          }]
                        }
                        """, MediaType.APPLICATION_JSON));

        var response = orders.capturePaypalOrder("ORDER-1");
        assertEquals("ORDER-1", response.id());
        assertEquals("COMPLETED", response.status());
        var unit = response.purchaseUnits().getFirst();
        assertEquals("7", unit.referenceId());
        var capture = unit.payments().captures().getFirst();
        assertEquals("CAPTURE-1", capture.id());
        assertEquals("COMPLETED", capture.status());
        assertEquals("20.00", capture.amount().value());
        assertEquals("USD", capture.amount().currencyCode());
        server.verify();
    }

    @Test
    void captureRejectsMissingOrderIdBeforeCallingPaypal() {
        for (String orderId : Arrays.asList(null, "", " ")) {
            assertThrows(InvalidPaypalRequestException.class, () -> orders.capturePaypalOrder(orderId));
        }
        verifyNoInteractions(tokens);
        server.verify();
    }

    @Test
    void captureMapsProviderFailures() {
        for (HttpStatus status : Arrays.asList(HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND, HttpStatus.UNPROCESSABLE_ENTITY,
                HttpStatus.TOO_MANY_REQUESTS, HttpStatus.INTERNAL_SERVER_ERROR)) {
            server.reset();
            server.expect(anything()).andRespond(withStatus(status));
            Class<? extends PaymentProviderException> expected = switch (status) {
                case BAD_REQUEST, NOT_FOUND, UNPROCESSABLE_ENTITY -> InvalidPaypalRequestException.class;
                case UNAUTHORIZED, FORBIDDEN -> InvalidPaypalCredentialException.class;
                case TOO_MANY_REQUESTS -> PaypalRateLimitException.class;
                default -> PaymentProviderException.class;
            };
            assertThrows(expected, () -> orders.capturePaypalOrder("ORDER-1"));
            server.verify();
        }
    }

    @Test
    void mapsProviderFailuresIncluding422And429() {
        for (HttpStatus status : Arrays.asList(HttpStatus.BAD_REQUEST, HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN, HttpStatus.UNPROCESSABLE_ENTITY, HttpStatus.TOO_MANY_REQUESTS,
                HttpStatus.INTERNAL_SERVER_ERROR)) {
            server.reset();
            server.expect(anything()).andRespond(withStatus(status));
            PaymentProviderException failure = assertThrows(PaymentProviderException.class,
                    () -> orders.createPaypalOrder(invoice, "USD"));
            assertFalse(failure.getMessage().isBlank());
            if (status == HttpStatus.UNPROCESSABLE_ENTITY) assertInstanceOf(InvalidPaypalRequestException.class, failure);
            if (status == HttpStatus.FORBIDDEN) assertInstanceOf(InvalidPaypalCredentialException.class, failure);
            if (status == HttpStatus.TOO_MANY_REQUESTS) assertInstanceOf(PaypalRateLimitException.class, failure);
            server.verify();
        }
    }

    @Test
    void rejectsInvalidTokensWithoutCachingThem() {
        PaypalTokenClient client = mock(PaypalTokenClient.class);
        PaypalAccessTokenProvider provider = new PaypalAccessTokenProvider(client);
        PaypalTokenResponse valid = token("valid", "Bearer", 3600);
        for (PaypalTokenResponse invalid : Arrays.asList(null, token(null, "Bearer", 3600),
                token(" ", "Bearer", 3600), token("value", null, 3600), token("value", "Basic", 3600),
                token("value", "Bearer", null), token("value", "Bearer", 0), token("value", "Bearer", 60))) {
            when(client.getAccessToken()).thenReturn(invalid);
            assertThrows(PaymentProviderException.class, provider::getValidAccessToken);
        }
        when(client.getAccessToken()).thenReturn(valid);
        assertEquals("valid", provider.getValidAccessToken());
        clearInvocations(client);
        assertEquals("valid", provider.getValidAccessToken());
        verifyNoInteractions(client);
    }

    @Test
    void expiredCachedTokenIsRefreshed() {
        PaypalTokenClient client = mock(PaypalTokenClient.class);
        when(client.getAccessToken()).thenReturn(token("first", "Bearer", 3600), token("second", "Bearer", 3600));
        PaypalAccessTokenProvider provider = new PaypalAccessTokenProvider(client);
        assertEquals("first", provider.getValidAccessToken());
        ReflectionTestUtils.setField(provider, "refreshAt", java.time.Instant.EPOCH);
        assertEquals("second", provider.getValidAccessToken());
    }

    @Test
    void tokenRequestUsesBasicAuthAndFormEncoding() {
        PaypalTokenClient client = new PaypalTokenClient(builder.build());
        ReflectionTestUtils.setField(client, "clientId", "client");
        ReflectionTestUtils.setField(client, "clientSecret", "secret");
        server.expect(requestTo("https://api-m.sandbox.paypal.com/v1/oauth2/token"))
                .andExpect(header("Authorization", "Basic Y2xpZW50OnNlY3JldA=="))
                .andExpect(content().string("grant_type=client_credentials"))
                .andRespond(withSuccess("{\"access_token\":\"token\",\"token_type\":\"Bearer\",\"expires_in\":3600}", MediaType.APPLICATION_JSON));
        assertEquals("token", client.getAccessToken().accessToken());
        server.verify();
    }

    @Test
    void rejectsInfiniteOrNegativeTimeouts() {
        RestClientConfig config = new RestClientConfig();
        assertThrows(IllegalArgumentException.class, () -> config.restClient(0, 1000));
        assertThrows(IllegalArgumentException.class, () -> config.restClient(1000, -1));
    }

    @Test
    void configuredReadTimeoutStopsAnUnresponsiveProvider() throws Exception {
        var release = new java.util.concurrent.CountDownLatch(1);
        var local = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        local.createContext("/", exchange -> {
            try {
                release.await(5, java.util.concurrent.TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        local.start();
        try {
            RestClientConfig config = new RestClientConfig();
            ReflectionTestUtils.setField(config, "paypalURL", "http://127.0.0.1:" + local.getAddress().getPort());
            RestClient client = config.restClient(1000, 100);
            ResourceAccessException failure = assertThrows(ResourceAccessException.class,
                    () -> client.get().retrieve().body(String.class));
            assertInstanceOf(java.net.SocketTimeoutException.class, failure.getCause());
        } finally {
            release.countDown();
            local.stop(0);
        }
    }

    private static PaypalTokenResponse token(String value, String type, Integer expiry) {
        return new PaypalTokenResponse(null, value, type, null, expiry, null);
    }
}
