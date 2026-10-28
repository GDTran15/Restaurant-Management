package com.duong.RestaurantManagement.payment.repository;

import com.duong.RestaurantManagement.payment.model.Payment;
import com.duong.RestaurantManagement.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaypalOrderId(String paypalOrderId);

    Optional<Payment> findFirstByInvoice_InvoiceIdAndPaymentStatusInOrderByPaymentIdDesc(Long invoiceId, List<PaymentStatus> statuses);

    boolean existsByInvoice_InvoiceIdAndPaymentStatusIn(Long invoiceId,List<PaymentStatus> statuses);

    boolean existsByInvoice_InvoiceIdAndPaymentStatus(Long invoiceId, PaymentStatus status);

    // Lấy id trước, rồi khóa invoice trước khi đọc Payment để tránh dữ liệu cũ khi chờ khóa.
    @Query("select p.invoice.invoiceId from Payment p where p.paypalOrderId = :paypalOrderId")
    Optional<Long> findInvoiceIdByPaypalOrderId(String paypalOrderId);
}
