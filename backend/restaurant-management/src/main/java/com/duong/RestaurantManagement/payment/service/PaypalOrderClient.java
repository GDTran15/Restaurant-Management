package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.payment.dto.paypal.*;
import com.duong.RestaurantManagement.payment.exception.InvalidPaypalCredentialException;
import com.duong.RestaurantManagement.payment.exception.InvalidPaypalRequestException;
import com.duong.RestaurantManagement.payment.exception.PaypalRateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaypalOrderClient {

    private final RestClient restClient;
    @Value("${app.frontend-url}")
    private String frontendUrl;
    private final PaypalAccessTokenProvider paypalAccessTokenProvider;


    public PaypalOrderResponse createPaypalOrder(Invoice invoice, String currency) {
        try {
            PaypalExperienceContext paypalExperienceContext = new PaypalExperienceContext(frontendUrl + "/payment/return",
                    frontendUrl + "/payment/cancel", "NO_SHIPPING");

            PaypalWallet paypalWallet = new PaypalWallet(paypalExperienceContext);

            PaypalPaymentSource paypalPaymentSource = new PaypalPaymentSource(paypalWallet);

            PaypalAmount paypalAmount = new PaypalAmount(currency, invoice.getTotalPay().toPlainString());

            PaypalPurchaseUnit paypalPurchaseUnit = new PaypalPurchaseUnit(invoice.getInvoiceId().toString(), paypalAmount);

            CreateOrderPaypalRequest createOrderPaypalRequest
                    = new CreateOrderPaypalRequest("CAPTURE", List.of(paypalPurchaseUnit), paypalPaymentSource);


            return restClient.post()
                             .uri("/v2/checkout/orders")
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




}
