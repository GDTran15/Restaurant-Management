package com.duong.RestaurantManagement.payment.dto.paypal;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateOrderPaypalRequest(
        String intent,
        @JsonProperty("purchase_units")
        List<PaypalPurchaseUnit> purchaseUnits,
        @JsonProperty("payment_source")
        PaypalPaymentSource paymentSource
) {
}
