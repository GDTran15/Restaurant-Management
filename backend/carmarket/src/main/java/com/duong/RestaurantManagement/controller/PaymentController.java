package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.payment.request.CreateOrderPaypalRequest;
import com.duong.RestaurantManagement.dto.payment.response.CaptureOrderPaypalResponse;
import com.duong.RestaurantManagement.dto.payment.response.CreateOrderPaypalResponse;
import com.duong.RestaurantManagement.model.Payment;
import com.duong.RestaurantManagement.service.PaymentService;
import com.paypal.sdk.models.LinkDescription;
import com.paypal.sdk.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<CreateOrderPaypalResponse> processPayment(
            @RequestBody CreateOrderPaypalRequest request
    ) {
        Order order = paymentService.createOrder(request, "paypal", "CAPTURE", "Pay invoice");

        String approvalUrl = order.getLinks()
                .stream()
                .filter(link -> "approve".equals(link.getRel()))
                .map(LinkDescription::getHref)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("PayPal approval link not found"));

        return ResponseEntity.ok(
                new CreateOrderPaypalResponse(
                        order.getId(),
                        approvalUrl
                )
        );
    }

    @PostMapping("/capture/{paypalOrderId}")
    public ResponseEntity<CaptureOrderPaypalResponse> processPaymentCapture(@PathVariable("paypalOrderId") String paypalOrderId) {
      Order order = paymentService.captureOrder(paypalOrderId);
      return ResponseEntity.ok(
                new CaptureOrderPaypalResponse(
                        order.getId(),
                        "SUCCESS",
                        "Payment capture successfully"
                )
        ) ;
    }


}
