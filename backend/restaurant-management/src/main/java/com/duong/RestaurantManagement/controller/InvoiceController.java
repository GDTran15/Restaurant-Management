package com.duong.RestaurantManagement.controller;


import com.duong.RestaurantManagement.dto.invoice.response.InvoiceResponseDTO;
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
    public ResponseEntity<InvoiceResponseDTO> addInvoice(
            @PathVariable Long tableId
            ) {

            return ResponseEntity.ok(invoiceService.createNewInvoice(tableId));
    }

    @PutMapping("/addMember/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> updateInvoiceWithMembership(@PathVariable Long invoiceId, @RequestParam String memberPhone){

        return ResponseEntity.ok( invoiceService.invoiceChangeAfterMember(invoiceId,memberPhone));
    }



}
