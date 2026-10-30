package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.payment.dto.paypal.*;
import com.duong.RestaurantManagement.payment.dto.response.PaypalOrderDetails;
import com.duong.RestaurantManagement.payment.exception.InvalidPaypalCredentialException;
import com.duong.RestaurantManagement.payment.exception.InvalidPaypalRequestException;
import com.duong.RestaurantManagement.payment.exception.PaymentProviderException;
import com.duong.RestaurantManagement.payment.exception.PaypalRateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class PaypalOrderClient {

    private final RestClient restClient;
    @Value("${app.frontend-url}")
    private String frontendUrl;
    private final PaypalAccessTokenProvider paypalAccessTokenProvider;
    private final PaymentReconciliationService paymentReconciliationService;


    public PaypalOrderResponse createPaypalOrder(Invoice invoice, String currency) {
        return createOrder(invoice.getInvoiceId(), invoice.getTotalPay(), currency, UUID.randomUUID().toString());
    }

    public PaypalOrderResponse createOrder(Long invoiceId, BigDecimal amount, String currency, String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new InvalidPaypalRequestException("PayPal create request ID is required");
        }
        try {
            PaypalExperienceContext paypalExperienceContext = new PaypalExperienceContext(frontendUrl + "/payment/return",
                    frontendUrl + "/payment/cancel", "NO_SHIPPING");

            PaypalWallet paypalWallet = new PaypalWallet(paypalExperienceContext);

            PaypalPaymentSource paypalPaymentSource = new PaypalPaymentSource(paypalWallet);

            PaypalAmount paypalAmount = new PaypalAmount(currency, amount.toPlainString());

            PaypalPurchaseUnit paypalPurchaseUnit = new PaypalPurchaseUnit(invoiceId.toString(), paypalAmount);

            CreateOrderPaypalRequest createOrderPaypalRequest
                    = new CreateOrderPaypalRequest("CAPTURE", List.of(paypalPurchaseUnit), paypalPaymentSource);


            return restClient.post()
                             .uri("/v2/checkout/orders")
                             .header("PayPal-Request-Id", requestId)
                             .headers(httpHeaders -> httpHeaders.setBearerAuth(paypalAccessTokenProvider.getValidAccessToken()))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(createOrderPaypalRequest)
                             .retrieve()
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 400, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal rejected the order request");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 401, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("PayPal rejected the access token");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 403, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("PayPal denied access to orders");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 422, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal could not process the order request");
                             })

                             .onStatus(httpStatusCode -> httpStatusCode.value() == 429, (request, response) -> {
                                 throw new PaypalRateLimitException("PayPal request limit exceeded; retry later");
                             })


                             .body(PaypalOrderResponse.class);
        } catch (ResourceAccessException e){
            throw new PaymentProviderException("Could not reach PayPal to create the order", e);
        } catch (RestClientException e) {
            throw new PaymentProviderException("PayPal order request failed", e);
        }
    }






    public PaypalCaptureOrderResponse capturePaypalOrder(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new InvalidPaypalRequestException("PayPal order ID is required");
        }

        try {
            return restClient.post()
                             .uri("/v2/checkout/orders/{orderId}/capture", orderId)
                             .header("PayPal-Request-Id", UUID.nameUUIDFromBytes(
                                     ("capture:" + orderId).getBytes(StandardCharsets.UTF_8)).toString())
                             .headers(httpHeaders -> httpHeaders.setBearerAuth(paypalAccessTokenProvider.getValidAccessToken()))
                             .header("Prefer", "return=representation")
                             .contentType(MediaType.APPLICATION_JSON)
                             .body("{}")
                             .retrieve()
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 400, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal rejected the capture request");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 401, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("PayPal rejected the access token");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 403, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("PayPal denied access to capture the order");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 404, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal order was not found");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 422, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal could not process the capture request");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 429, (request, response) -> {
                                 throw new PaypalRateLimitException("PayPal request limit exceeded; retry later");
                             })
                             .body(PaypalCaptureOrderResponse.class);
        } catch (ResourceAccessException e) {
            throw new PaymentProviderException("Could not reach PayPal to capture the order", e);
        } catch (RestClientException e) {
            throw new PaymentProviderException("PayPal capture request failed", e);
        }
    }


    public PaypalOrderDetails paypalOrderDetails(String orderId){
        if (orderId == null || orderId.isBlank()){
            throw new InvalidPaypalRequestException("Order id cannot be null");
        }

        try {
            PaypalOrderDetails details = restClient.get()
                             .uri("/v2/checkout/orders/{orderId}", orderId)
                             .headers(httpHeaders -> httpHeaders.setBearerAuth(paypalAccessTokenProvider.getValidAccessToken()))
                             .retrieve()
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 400, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal rejected the get request");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 401, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("PayPal rejected the access token");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 403, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("PayPal denied access to get the order");
                             })
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 404, (request, response) -> {
                                 throw new InvalidPaypalRequestException("PayPal order was not found");
                             })

                             .body(PaypalOrderDetails.class);

            validatePaypalOrderDetails(details,orderId);

            return details;
        } catch  (ResourceAccessException e) {
            throw new PaymentProviderException("Could not reach PayPal to get the order", e);
        } catch (RestClientException e) {
            throw new PaymentProviderException("PayPal get request failed", e);
        }
    }


    private void validatePaypalOrderDetails(
            PaypalOrderDetails details,
            String requestedOrderId
    ) {
        if (details == null) {
            throw new PaymentProviderException(
                    "PayPal returned an empty order response"
            );
        }

        if (details.id() == null || details.id().isBlank()) {
            throw new PaymentProviderException(
                    "PayPal response is missing order ID"
            );
        }

        if (!details.id().equals(requestedOrderId)) {
            throw new PaymentProviderException(
                    "PayPal returned an unexpected order ID"
            );
        }

        if (details.status() == null || details.status().isBlank()) {
            throw new PaymentProviderException(
                    "PayPal response is missing order status"
            );
        }
    }
}
