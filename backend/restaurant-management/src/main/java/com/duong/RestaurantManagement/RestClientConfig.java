package com.duong.RestaurantManagement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestClientConfig {

    @Value("${paypal.base-url}")
    private String paypalURL;

    @Bean
    public RestClient restClient(
            @Value("${paypal.connect-timeout-ms:5000}") int connectTimeout,
            @Value("${paypal.read-timeout-ms:30000}") int readTimeout){
        if (connectTimeout <= 0 || readTimeout <= 0) {
            throw new IllegalArgumentException("PayPal timeouts must be positive");
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return RestClient.builder().baseUrl(paypalURL).requestFactory(factory).build();
    }
}
