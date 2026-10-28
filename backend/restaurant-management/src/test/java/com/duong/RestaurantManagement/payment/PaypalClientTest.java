package com.duong.RestaurantManagement.payment;

import com.duong.RestaurantManagement.RestClientConfig;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalTokenResponse;
import com.duong.RestaurantManagement.payment.exception.*;
import com.duong.RestaurantManagement.payment.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
