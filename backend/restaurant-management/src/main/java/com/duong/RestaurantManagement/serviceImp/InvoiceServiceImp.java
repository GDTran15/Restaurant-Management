package com.duong.RestaurantManagement.serviceImp;


import com.duong.RestaurantManagement.dto.invoice.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.exception.InvoiceHasBeenPaidException;
import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.mapper.InvoiceMapper;
import com.duong.RestaurantManagement.model.*;
import com.duong.RestaurantManagement.repo.*;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.InvoiceService;
import com.duong.RestaurantManagement.service.MemberService;
import com.duong.RestaurantManagement.service.RestaurantTableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImp implements InvoiceService {

    private final DiningSessionRepo diningSessionRepo;

    private final DiningSessionService   diningSessionService;

    private final MemberRepo memberRepo;

    private final MembershipRepo membershipRepo;

    private final InvoiceRepo invoiceRepo;

    private final MemberService memberService;

    private final RestaurantTableRepo restaurantTableRepo;

    private final RestaurantTableService restaurantTableService;



    @Transactional
    @Override
    public InvoiceResponseDTO createNewInvoice(Long tableId) {
        DiningSession diningSession = diningSessionRepo.findByDiningStatusAndRestaurantTable_RestaurantTableId(DiningStatus.ACTIVE,tableId)
                .orElseThrow(() -> new RuntimeException("No dining session found for this table"));


        double diningSessionPrice = diningSessionService.getDiningSessionTotalOrderPrice(diningSession.getDiningSessionId());
        Invoice invoice = Invoice.builder()
                .payBeforeDiscount(diningSessionPrice)
                .discountAmount(0)
                .totalPay(diningSessionPrice - 0)
                .diningSession(diningSession)
                .createdAt(LocalDateTime.now())
                .invoiceStatus(InvoiceStatus.UNPAID)
                .build();
        invoiceRepo.save(invoice);
        diningSessionService.deactiveDinningSession(diningSession.getDiningSessionId());
        restaurantTableService.changeTableStatus(restaurantTableRepo.findById(tableId).orElseThrow(
                () -> new ResourceNotFoundException("No restaurant table found ")
        ));
        return InvoiceMapper.invoiceToCreateInvoiceResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponseDTO invoiceChangeAfterMember(Long invoiceId, String memberPhone) {
        Invoice invoice = invoiceRepo.findById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException("No invoice found ")
        );
        checkIfInvoiceIsPaid(invoice.getInvoiceStatus(), "Cannot add member after invoice been paid");

        Member member = memberRepo.findByMemberPhone(memberPhone).orElseThrow(
                () -> new ResourceNotFoundException("No member found ")
        );
        invoice.setMember(member);
        applyMemberDiscount(invoice,member);

        invoiceRepo.save(invoice);

        return InvoiceMapper.invoiceToCreateInvoiceResponse(invoice);
    }

    private void applyMemberDiscount(Invoice invoice, Member member) {
        double discountRate = membershipRepo
                .findMembershipDiscountRateByMembershipRank(member.getMemberRank());

        double discountAmount = invoice.getPayBeforeDiscount() * discountRate;

        invoice.setDiscountAmount(discountAmount);
        invoice.setTotalPay(invoice.getPayBeforeDiscount() - discountAmount);
    }

    @Override
    @Transactional
    public void markInvoiceAsPaid(Invoice invoice)  {
        checkIfInvoiceIsPaid(invoice.getInvoiceStatus(), "Invoice has been paid");
        invoice.setInvoiceStatus(InvoiceStatus.PAID);
        if (invoice.getMember() != null) {

            memberService.updateMemberAfterPayment(invoice.getMember(), invoice.getTotalPay());

        }
        invoiceRepo.save(invoice);
    }

    private void checkIfInvoiceIsPaid( InvoiceStatus  invoiceStatus, String message) {
        if (invoiceStatus == InvoiceStatus.PAID){
            throw new InvoiceHasBeenPaidException(message);
        }
    }
}
