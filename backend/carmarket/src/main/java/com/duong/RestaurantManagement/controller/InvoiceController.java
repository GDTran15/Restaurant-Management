package com.duong.RestaurantManagement.controller;


import com.duong.RestaurantManagement.model.Invoice;
import com.duong.RestaurantManagement.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invoices")
public class InvoiceController {


    private InvoiceService invoiceService;

    @PostMapping("/{diningSessionId}")
    public ResponseEntity<Void> addInvoice(
            @PathVariable Long diningSessionId
            , @RequestParam int memberPhoneNumber) {
            invoiceService.createNewInvoice(diningSessionId,memberPhoneNumber);
            return ResponseEntity.noContent().build();
    }
}
