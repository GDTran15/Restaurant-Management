package com.duong.RestaurantManagement.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaypalCaptureOrderResponse(
        String id,
        String status,
        @JsonProperty("purchase_units") List<PurchaseUnit> purchaseUnits
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PurchaseUnit(
            @JsonProperty("reference_id") String referenceId,
            Payments payments
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Payments(List<Capture> captures) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Capture(String id, String status, PaypalAmount amount) {}
}
