package com.duong.RestaurantManagement.payment.controller;

import com.duong.RestaurantManagement.payment.dto.request.CashPaymentRequest;
import com.duong.RestaurantManagement.payment.dto.request.CreateOrderPaypalRequest;
import com.duong.RestaurantManagement.payment.dto.response.CaptureOrderPaypalResponse;
import com.duong.RestaurantManagement.payment.dto.response.CreateOrderPaypalResponse;
import com.duong.RestaurantManagement.payment.service.PaymentService;
import com.paypal.sdk.models.LinkDescription;
import com.paypal.sdk.models.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final com.duong.RestaurantManagement.auth.service.SessionAccess sessionAccess;

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<CreateOrderPaypalResponse> processPayment(
            @jakarta.validation.Valid @RequestBody CreateOrderPaypalRequest request
    ) {
        sessionAccess.requireInvoice(request.invoiceId());
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
      sessionAccess.requirePayment(paypalOrderId);
      Order order = paymentService.captureOrder(paypalOrderId);
      return ResponseEntity.ok(
                new CaptureOrderPaypalResponse(
                        order.getId(),
                        "SUCCESS",
                        "Payment capture successfully"
                )
        ) ;
    }

    @PostMapping("/cash")
    public ResponseEntity<Void> processCashPayment(@Valid @RequestBody CashPaymentRequest request) {
        paymentService.createCashPayment(request);
        return ResponseEntity.noContent().build();
    }

}
