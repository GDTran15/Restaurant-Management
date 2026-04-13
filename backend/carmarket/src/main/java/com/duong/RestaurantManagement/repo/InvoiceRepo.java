package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
}
