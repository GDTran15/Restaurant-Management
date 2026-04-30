package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
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
    public void createNewInvoice(Long tableId) {
        DiningSession diningSession = diningSessionRepo.findByDiningStatusAndRestaurantTable_RestaurantTableId(DiningStatus.ACTIVE,tableId)
                .orElseThrow(() -> new RuntimeException("No dining session found for this table"));
        diningSessionService.deactiveDinningSession(diningSession.getDiningSessionId());

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
        restaurantTableService.changeTableStatus(restaurantTableRepo.findById(tableId).orElseThrow(
                () -> new ResourceNotFoundException("No restaurant table found ")
        ));
    }

    @Override
    @Transactional
    public void invoiceChangeAfterMember(Long invoiceId, int memberPhone) {
        Invoice invoice = invoiceRepo.findById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException("No invoice found ")
        );
        Member member = memberRepo.findByMemberPhone(memberPhone).orElseThrow(
                () -> new ResourceNotFoundException("No member found ")
        );
        invoice.setMember(member);

        double discountAmount = invoice.getPayBeforeDiscount() * membershipRepo.findMembershipDiscountRateByMembershipRank(member.getMemberRank());
        invoice.setDiscountAmount(discountAmount);
        invoice.setTotalPay(invoice.getPayBeforeDiscount() - discountAmount);
        invoiceRepo.save(invoice);

    }

    @Override
    public void markInvoiceAsPaid(Invoice invoice) {
        invoice.setInvoiceStatus(InvoiceStatus.PAID);
        memberService.updateMemberAfterPayment(invoice.getMember(),invoice.getTotalPay());
        invoiceRepo.save(invoice);

    }
}
