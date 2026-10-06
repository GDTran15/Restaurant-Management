package com.duong.RestaurantManagement.payment.repository;

import com.duong.RestaurantManagement.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    boolean existsByInvoice_InvoiceIdAndPaymentStatus(Long id, com.duong.RestaurantManagement.payment.model.PaymentStatus status);
    java.util.List<Payment> findByInvoice_InvoiceIdAndPaymentStatus(Long id, com.duong.RestaurantManagement.payment.model.PaymentStatus status);
    @org.springframework.data.jpa.repository.Query("select p.invoice.invoiceId from Payment p where p.paypalOrderId = :id")
    Optional<Long> findInvoiceIdByPaypalOrderId(@org.springframework.data.repository.query.Param("id") String id);
    Optional<Payment> findByPaypalOrderId(String paypalOrderId);
}
