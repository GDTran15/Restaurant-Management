package com.duong.RestaurantManagement.invoice.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.dining.repository.DiningSessionRepo;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.invoice.model.InvoiceStatus;
import com.duong.RestaurantManagement.invoice.repository.InvoiceRepo;
import com.duong.RestaurantManagement.member.model.Member;
import com.duong.RestaurantManagement.member.repository.MemberRepo;
import com.duong.RestaurantManagement.member.repository.MembershipRepo;
import com.duong.RestaurantManagement.member.service.MemberService;
import com.duong.RestaurantManagement.order.service.OrderService;
import com.duong.RestaurantManagement.payment.model.PaymentStatus;
import com.duong.RestaurantManagement.payment.repository.PaymentRepository;
import com.duong.RestaurantManagement.table.repository.RestaurantTableRepo;
import com.duong.RestaurantManagement.table.service.RestaurantTableService;


import com.duong.RestaurantManagement.invoice.dto.response.InvoiceResponseDTO;
import com.duong.RestaurantManagement.invoice.dto.response.UpdateInvoiceMemberResponse;
import com.duong.RestaurantManagement.order.dto.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.invoice.exception.InvoiceHasBeenPaidException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.invoice.mapper.InvoiceMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

    private final OrderService orderService;

    private final PaymentRepository paymentRepo;


    @Transactional
    @Override
    public InvoiceResponseDTO createNewInvoice(Long tableId) {
        DiningSession diningSession = diningSessionRepo.findFirstByRestaurantTable_RestaurantTableIdAndDiningStatusNot(tableId, DiningStatus.COMPLETED)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No dining session found for this table"));

        if (orderService.hasActiveOrderByDiningSession(diningSession.getDiningSessionId())){
            throw new InvalidOrderStateException("There still active order in dining session");
        }

        List<GetOrderItemDTO> orderItems = diningSessionService
                .getDiningSessionOrderItems(diningSession.getDiningSessionId());

        Optional<Invoice> unpaidInvoice = invoiceRepo.findFirstByDiningSession_DiningSessionIdAndInvoiceStatusOrderByCreatedAtDesc(
                diningSession.getDiningSessionId(),
                InvoiceStatus.UNPAID
        );
        if (unpaidInvoice.isPresent()) {
            return InvoiceMapper.invoiceToCreateInvoiceResponse(unpaidInvoice.get(), orderItems);
        }

        BigDecimal diningSessionPrice = diningSessionService.getDiningSessionTotalOrderPrice(diningSession.getDiningSessionId());

        Invoice invoice = Invoice.builder()
                .payBeforeDiscount(diningSessionPrice)
                .discountAmount(BigDecimal.ZERO)
                .totalPay(diningSessionPrice)
                .diningSession(diningSession)
                .createdAt(LocalDateTime.now())
                .invoiceStatus(InvoiceStatus.UNPAID)
                .build();
        invoiceRepo.save(invoice);

        diningSession.setDiningStatus(DiningStatus.WAITING_FOR_PAYMENT);


        return InvoiceMapper.invoiceToCreateInvoiceResponse(invoice,  orderItems);
    }

    @Override
    @Transactional
    public UpdateInvoiceMemberResponse invoiceChangeAfterMember(Long invoiceId, String memberPhone) {
        Invoice invoice = invoiceRepo.findLockedById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException("No invoice found ")
        );
        checkIfInvoiceCanChange(invoice.getInvoiceStatus(), "Cannot add member after invoice is closed");


        Member member = memberRepo.findByMemberPhone(memberPhone).orElseThrow(
                () -> new ResourceNotFoundException("No member found ")
        );
        requireNoPaymentAttempt(invoiceId);
        invoice.setMember(member);
        applyMemberDiscount(invoice,member);

        invoiceRepo.save(invoice);

        return InvoiceMapper.mapToUpdateInvoiceMemberResponse(invoice,member);
    }

    private void applyMemberDiscount(Invoice invoice, Member member) {
        BigDecimal discountRate = membershipRepo
                .findMembershipDiscountRateByMembershipRank(member.getMemberRank());

        if (discountRate == null) discountRate = BigDecimal.ZERO;
        BigDecimal discountAmount = invoice.getPayBeforeDiscount().multiply(discountRate).setScale(2, java.math.RoundingMode.HALF_UP);

        invoice.setDiscountAmount(discountAmount);
        invoice.setTotalPay(invoice.getPayBeforeDiscount().subtract(discountAmount));
    }

    @Override
    @Transactional
    public void markInvoiceAsPaid(Invoice invoice)  {
        checkIfInvoiceCanChange(invoice.getInvoiceStatus(), "Invoice cannot be paid");
        if (orderService.hasActiveOrderByDiningSession(invoice.getDiningSession().getDiningSessionId())) {
            throw new InvalidOrderStateException("Session still has unfinished orders");
        }
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        if (invoice.getMember() != null) {

            memberService.updateMemberAfterPayment(invoice.getMember(), invoice.getTotalPay());

        }
        diningSessionService.deactiveDinningSession(invoice.getDiningSession().getDiningSessionId());

        invoiceRepo.save(invoice);
    }

    @Override
    public List<InvoiceResponseDTO> getTodayInvoice() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return invoiceRepo.findListOfTodayInvoice(startOfDay, endOfDay);
    }

    @Override
    public InvoiceResponseDTO getInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepo.findById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException("No invoice found ")
        );

        List<GetOrderItemDTO> invoiceItems = diningSessionService.getDiningSessionOrderItems(invoice.getDiningSession().getDiningSessionId());
        return InvoiceMapper.invoiceToCreateInvoiceResponse(
                invoice, invoiceItems
        );
    }

    @Override
    public Optional<InvoiceResponseDTO> getActiveUnpaidInvoice(Long diningSessionId) {
        return invoiceRepo.findFirstByDiningSession_DiningSessionIdAndInvoiceStatusOrderByCreatedAtDesc(
                diningSessionId,
                InvoiceStatus.UNPAID
        ).map((invoice) -> {
            List<GetOrderItemDTO> invoiceItems = diningSessionService.getDiningSessionOrderItems(diningSessionId);
            return InvoiceMapper.invoiceToCreateInvoiceResponse(invoice, invoiceItems);
        });
    }

    @Override
    @Transactional
    public InvoiceResponseDTO cancelInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepo.findLockedById(invoiceId).orElseThrow(
                () -> new ResourceNotFoundException("No invoice found ")
        );

        checkIfInvoiceCanChange(invoice.getInvoiceStatus(), "Only unpaid invoices can be cancelled");

        requireNoPaymentAttempt(invoiceId);

        invoice.setInvoiceStatus(InvoiceStatus.CANCELLED);

        diningSessionRepo.findLockedById(invoice.getDiningSession().getDiningSessionId()).orElseThrow()
                .setDiningStatus(DiningStatus.ACTIVE);

        invoiceRepo.save(invoice);

        List<GetOrderItemDTO> invoiceItems = diningSessionService.getDiningSessionOrderItems(invoice.getDiningSession().getDiningSessionId());

        return InvoiceMapper.invoiceToCreateInvoiceResponse(invoice, invoiceItems);
    }

    private void requireNoPaymentAttempt(Long invoiceId) {
        // PENDING có thể là timeout: không đổi tổng tiền/hủy invoice khi chưa đối soát.
        if (paymentRepo.existsByInvoice_InvoiceIdAndPaymentStatusIn(invoiceId, List.of(
                PaymentStatus.PENDING, PaymentStatus.CAPTURING)))
            throw new InvalidOrderStateException("PayPal checkout has started; finish or reconcile it before changing this invoice");
    }
    private void checkIfInvoiceCanChange( InvoiceStatus  invoiceStatus, String message) {
        if (invoiceStatus != InvoiceStatus.UNPAID){
            throw new InvoiceHasBeenPaidException(message);
        }
    }
}
