package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.payment.dto.paypal.PaypalTokenResponse;
import com.duong.RestaurantManagement.payment.exception.InvalidPaypalCredentialException;
import com.duong.RestaurantManagement.payment.exception.PaymentProviderException;
import com.duong.RestaurantManagement.payment.exception.PaypalRateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


@Service
@RequiredArgsConstructor
public class PaypalTokenClient {
    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.secret-key}")
    private String clientSecret;

    private final RestClient restClient;

    public PaypalTokenResponse getAccessToken(){

        try {


            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");

            return restClient.post()
                             .uri("/v1/oauth2/token")
                             .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                             .headers(h -> h.setBasicAuth(clientId, clientSecret))
                             .body(formData)
                             .retrieve()
                             .onStatus(httpStatusCode -> httpStatusCode.value() == 401, (request, response) -> {
                                 throw new InvalidPaypalCredentialException("Your client Id and secret is invalid");
                             }).onStatus(httpStatusCode -> httpStatusCode.value() == 429, (request, response) -> {
                        throw new PaypalRateLimitException("Too much paypal request");
                    }).body(PaypalTokenResponse.class);

        } catch (ResourceAccessException ex){
            throw new PaymentProviderException("Could not reach PayPal to obtain an access token", ex);
        } catch (RestClientException ex) {
            throw new PaymentProviderException("PayPal token request failed", ex);
        }
    }

}
