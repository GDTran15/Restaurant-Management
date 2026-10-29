package com.duong.RestaurantManagement.payment.model;

import com.duong.RestaurantManagement.invoice.model.Invoice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payments")
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private LocalDateTime paidAt;

    @Column(unique = true)
    private String paypalOrderId;

    @Column(unique = true, length = 36)
    private String paypalCreateRequestId;

    private Instant paypalCreateRequestedAt;

    @Column(length = 2048)
    private String paypalApprovalUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, length = 3)
    private String currency;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cashChange = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
}
