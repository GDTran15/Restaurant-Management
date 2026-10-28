package com.duong.RestaurantManagement.payment.dto.paypal;

import java.util.List;

public record PaypalOrderResponse (
        String id,
        String status,
        List<PaypalLink> links
){
}
