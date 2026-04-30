package com.duong.RestaurantManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "invoices")
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private double payBeforeDiscount;

    private double discountAmount;

    private double totalPay;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;


    @ManyToOne
    @JoinColumn(name = "dining_session_id")
    private DiningSession diningSession;

    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
