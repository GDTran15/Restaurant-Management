package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.model.*;
import com.duong.RestaurantManagement.repo.DiningSessionRepo;
import com.duong.RestaurantManagement.repo.InvoiceRepo;
import com.duong.RestaurantManagement.repo.MemberRepo;
import com.duong.RestaurantManagement.repo.MembershipRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.InvoiceService;
import com.duong.RestaurantManagement.service.MemberService;
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



    @Transactional
    @Override
    public void createNewInvoice(Long tableId,int memberPhoneNumber) {
        DiningSession diningSession = diningSessionRepo.findByDiningStatusAndRestaurantTable_RestaurantTableId(DiningStatus.ACTIVE,tableId)
                .orElseThrow(() -> new RuntimeException("No dining session found for this table"));

        double diningSessionPrice = diningSessionService.getDiningSessionTotalOrderPrice(diningSession.getDiningSessionId());

        Member member = memberRepo.findByMemberPhone(memberPhoneNumber).orElseThrow(() ->
                new ResourceNotFoundException("No member found"));

        double discountAmount = membershipRepo.findMembershipDiscountRateByMembershipRank(member.getMemberRank()) * diningSessionPrice;

        Invoice invoice = Invoice.builder()
                .payBeforeDiscount(diningSessionPrice)
                .discountAmount(discountAmount)
                .totalPay(diningSessionPrice - discountAmount)
                .createdAt(LocalDateTime.now())
                .invoiceStatus(InvoiceStatus.UNPAID)
                .build();

        invoiceRepo.save(invoice);
    }
}
