package com.duong.RestaurantManagement.payment.config;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfiguration {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.secret-key}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public PaypalServerSdkClient paypalClient () {
          return new PaypalServerSdkClient.Builder()
                .httpClientConfig(config -> config.timeout(30000))
                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(
                        clientId,
                        clientSecret
                )
                        .build())
                .environment(Environment.valueOf(mode.toUpperCase(java.util.Locale.ROOT)))
                .build();
    }
}
