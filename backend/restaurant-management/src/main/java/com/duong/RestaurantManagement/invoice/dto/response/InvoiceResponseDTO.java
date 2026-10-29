package com.duong.RestaurantManagement.invoice.dto.response;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.invoice.model.InvoiceStatus;

import java.util.List;

public record InvoiceResponseDTO(
        Long invoiceId,
        BigDecimal payBeforeDiscount,
        BigDecimal discountAmount,
        BigDecimal totalPay,
        InvoiceStatus invoiceStatus,
        Long diningSessionId,
        List<GetOrderItemDTO> orderItems


) {
}
