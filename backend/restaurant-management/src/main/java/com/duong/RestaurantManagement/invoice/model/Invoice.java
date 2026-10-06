package com.duong.RestaurantManagement.invoice.model;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.member.model.Member;
import com.duong.RestaurantManagement.payment.model.Payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "invoices")
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal payBeforeDiscount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPay;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dining_session_id", nullable = false)
    private DiningSession diningSession;

    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
