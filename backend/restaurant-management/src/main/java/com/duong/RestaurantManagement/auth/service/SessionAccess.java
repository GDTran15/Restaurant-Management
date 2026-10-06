package com.duong.RestaurantManagement.auth.service;
import com.duong.RestaurantManagement.invoice.repository.InvoiceRepo;
import com.duong.RestaurantManagement.payment.repository.PaymentRepo;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionAccess {
    private final InvoiceRepo invoiceRepo;
    private final PaymentRepo paymentRepo;
    public void requireSession(Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"))) return;
        if (auth == null || !auth.isAuthenticated() || id == null || !id.toString().equals(auth.getName())
                || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            throw new AccessDeniedException("This dining session is not yours");
        }
    }
    public void requireInvoice(Long id) {
        requireSession(invoiceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"))
                .getDiningSession().getDiningSessionId());
    }
    public void requirePayment(String paypalId) {
        requireInvoice(paymentRepo.findByPaypalOrderId(paypalId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found")).getInvoice().getInvoiceId());
    }
}
