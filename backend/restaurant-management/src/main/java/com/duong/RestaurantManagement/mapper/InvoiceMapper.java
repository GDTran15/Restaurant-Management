package com.duong.RestaurantManagement.mapper;


import com.duong.RestaurantManagement.dto.invoice.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.model.Invoice;

public class InvoiceMapper {

    public static InvoiceResponseDTO invoiceToCreateInvoiceResponse(Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getInvoiceId(),
                invoice.getPayBeforeDiscount(),
                invoice.getDiscountAmount() ,
                invoice.getTotalPay(),
                invoice.getInvoiceStatus()
        );
    }
}
