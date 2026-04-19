package com.duong.RestaurantManagement.service;

public interface InvoiceService {
    void createNewInvoice(Long dinningSessionId);

    void invoiceChangeAfterMember(Long invoiceId, int memberGmail);
}
