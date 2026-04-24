package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.payment.response.GetAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PaypalAuthenticationService {

    @Value("${paypal.client-id}")
    private String paypalClientId;

    @Value("${paypal.secret-key}")
    private String paypalClientSecret;

    public GetAccessTokenResponse getAccessToken() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-m.sandbox.paypal.com")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setBasicAuth(paypalClientId, paypalClientSecret);
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })

                .build();

        return webClient.post()
                .uri("/v1/oauth2/token")
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(GetAccessTokenResponse.class)// turn response into String
                .block();
    }
}
