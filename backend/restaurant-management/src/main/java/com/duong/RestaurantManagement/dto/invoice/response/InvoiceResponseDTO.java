package com.duong.RestaurantManagement.dto.invoice.response;

import com.duong.RestaurantManagement.model.InvoiceStatus;

public record InvoiceResponseDTO(
        Long invoiceId,
        double payBeforeDiscount,
        double discountAmount,
        double totalPay,
        InvoiceStatus invoiceStatus
) {
}
