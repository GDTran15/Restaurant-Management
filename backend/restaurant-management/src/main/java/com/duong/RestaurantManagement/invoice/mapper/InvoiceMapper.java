package com.duong.RestaurantManagement.invoice.mapper;


import com.duong.RestaurantManagement.invoice.dto.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.invoice.dto.response.UpdateInvoiceMemberResponse;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.member.model.Member;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class InvoiceMapper {

    public static InvoiceResponseDTO invoiceToCreateInvoiceResponse(Invoice invoice, List<GetOrderItemDTO> orderItems) {
        return new InvoiceResponseDTO(
                invoice.getInvoiceId(),
                invoice.getPayBeforeDiscount(),
                invoice.getDiscountAmount() ,
                invoice.getTotalPay(),
                invoice.getInvoiceStatus(),
                invoice.getDiningSession().getDiningSessionId(),
                orderItems

        );
    }

    public static UpdateInvoiceMemberResponse mapToUpdateInvoiceMemberResponse(Invoice invoice, Member member) {
        return new UpdateInvoiceMemberResponse(
                invoice.getInvoiceId(),
                invoice.getDiscountAmount(),
                invoice.getTotalPay(),
                member.getFirstName(),
                member.getLastName(),
                member.getMemberRank()
        );
    }
}
