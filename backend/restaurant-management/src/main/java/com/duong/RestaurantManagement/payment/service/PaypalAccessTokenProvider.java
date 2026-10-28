package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.payment.dto.paypal.PaypalTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaypalAccessTokenProvider {

    private final PaypalTokenClient paypalTokenClient;

    private String cachedToken;
    private Instant refreshAt;

    public synchronized String getValidAccessToken(){
        if (cachedToken != null){
            Instant now = Instant.now();

            if (!now.isBefore(refreshAt)) {
                newToken();
            }
            return cachedToken;
        }
        newToken();
        return cachedToken;

    }

    private void newToken(){
        PaypalTokenResponse paypalTokenResponse = paypalTokenClient.getAccessToken();
        if (paypalTokenResponse == null || paypalTokenResponse.accessToken() == null
                || paypalTokenResponse.accessToken().isBlank()
                || !"Bearer".equalsIgnoreCase(paypalTokenResponse.tokenType())
                || paypalTokenResponse.expiresIn() == null || paypalTokenResponse.expiresIn() <= 60){
            throw new PaymentProviderException("PayPal returned an invalid token response");
        }
        refreshAt = Instant.now().plusSeconds(paypalTokenResponse.expiresIn() - 60);
        cachedToken = paypalTokenResponse.accessToken();

    }

}
