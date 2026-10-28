package com.duong.RestaurantManagement.payment.service;

import com.duong.RestaurantManagement.invoice.repository.InvoiceRepo;
import com.duong.RestaurantManagement.invoice.service.InvoiceService;
import com.duong.RestaurantManagement.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepo invoiceRepository;
    private final InvoiceService invoiceService;

}
