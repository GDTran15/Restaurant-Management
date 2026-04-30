package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaypalOrderId(String paypalOrderId);
}
