package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.model.Invoice;

public interface InvoiceService {
    void createNewInvoice(Long dinningSessionId);

    void invoiceChangeAfterMember(Long invoiceId, int memberGmail);

    void markInvoiceAsPaid(Invoice invoice);
}
