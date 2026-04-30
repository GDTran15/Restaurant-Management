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


    private final InvoiceService invoiceService;

    @PostMapping("/{tableId}")
    public ResponseEntity<Void> addInvoice(
            @PathVariable Long tableId
            ) {
            invoiceService.createNewInvoice(tableId);
            return ResponseEntity.noContent().build();
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<Void> updateInvoiceWithMembership(@PathVariable Long invoiceId, @RequestParam int memberPhone){
        invoiceService.invoiceChangeAfterMember(invoiceId,memberPhone);
        return ResponseEntity.noContent().build();
    }



}
