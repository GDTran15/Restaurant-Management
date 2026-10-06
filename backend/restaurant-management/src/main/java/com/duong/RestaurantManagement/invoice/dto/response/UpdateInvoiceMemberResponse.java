package com.duong.RestaurantManagement.invoice.dto.response;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.invoice.model.InvoiceStatus;
import com.duong.RestaurantManagement.member.model.MembershipRank;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record UpdateInvoiceMemberResponse (
        Long invoiceId,
        BigDecimal discountAmount,
        BigDecimal totalPay,
        String memberFirstName,
        String memberLastName,
        MembershipRank membershipRank
) {}
