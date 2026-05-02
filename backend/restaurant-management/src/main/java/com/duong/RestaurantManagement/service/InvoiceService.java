package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.invoice.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.model.Invoice;
import org.apache.coyote.BadRequestException;

public interface InvoiceService {
    InvoiceResponseDTO createNewInvoice(Long dinningSessionId);

    InvoiceResponseDTO invoiceChangeAfterMember(Long invoiceId, String memberPhone);

    void markInvoiceAsPaid(Invoice invoice) throws BadRequestException;
}
