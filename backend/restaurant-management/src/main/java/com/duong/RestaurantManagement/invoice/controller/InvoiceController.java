package com.duong.RestaurantManagement.invoice.controller;


import com.duong.RestaurantManagement.auth.service.SessionAccess;
import com.duong.RestaurantManagement.invoice.dto.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.invoice.dto.response.UpdateInvoiceMemberResponse;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.invoice.service.InvoiceService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invoices")
@Validated
public class InvoiceController {
    private final SessionAccess sessionAccess;

    private final InvoiceService invoiceService;

    @GetMapping("")
    public ResponseEntity<List<InvoiceResponseDTO>> getTodayInvoices() {
        return ResponseEntity.ok(invoiceService.getTodayInvoice());
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable Long invoiceId) {
        sessionAccess.requireInvoice(invoiceId);
        return ResponseEntity.ok(invoiceService.getInvoice(invoiceId));
    }

    @GetMapping("/active")
    public ResponseEntity<InvoiceResponseDTO> getActiveInvoice(@RequestParam Long diningSessionId) {
        sessionAccess.requireSession(diningSessionId);
        return invoiceService.getActiveUnpaidInvoice(diningSessionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }


    @PostMapping("/{tableId}")
    public ResponseEntity<InvoiceResponseDTO> addInvoice(
            @PathVariable Long tableId
            ) {

            return ResponseEntity.ok(invoiceService.createNewInvoice(tableId));
    }

    @PutMapping("/addMember/{invoiceId}")
    public ResponseEntity<UpdateInvoiceMemberResponse> updateInvoiceWithMembership(@PathVariable Long invoiceId,
                                                                                   @RequestParam
                                                                          @NotBlank(message = "Enter member Phone number")
                                                                          String memberPhone){

        return ResponseEntity.ok( invoiceService.invoiceChangeAfterMember(invoiceId,memberPhone));
    }

    @PatchMapping("/{invoiceId}/cancel")
    public ResponseEntity<InvoiceResponseDTO> cancelInvoice(@PathVariable Long invoiceId){
        return ResponseEntity.ok(invoiceService.cancelInvoice(invoiceId));
    }


}
