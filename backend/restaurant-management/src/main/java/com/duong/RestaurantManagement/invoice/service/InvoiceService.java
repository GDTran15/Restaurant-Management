package com.duong.RestaurantManagement.invoice.service;

import com.duong.RestaurantManagement.invoice.dto.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.invoice.dto.response.UpdateInvoiceMemberResponse;
import com.duong.RestaurantManagement.invoice.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    InvoiceResponseDTO createNewInvoice(Long dinningSessionId);

    UpdateInvoiceMemberResponse invoiceChangeAfterMember(Long invoiceId, String memberPhone);

    void markInvoiceAsPaid(Invoice invoice);

     List<InvoiceResponseDTO> getTodayInvoice();

     InvoiceResponseDTO getInvoice(Long invoiceId);

     Optional<InvoiceResponseDTO> getActiveUnpaidInvoice(Long diningSessionId);

     InvoiceResponseDTO cancelInvoice(Long invoiceId);
}
