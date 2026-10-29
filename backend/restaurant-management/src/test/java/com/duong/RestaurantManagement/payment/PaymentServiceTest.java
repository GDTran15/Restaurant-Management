package com.duong.RestaurantManagement.payment;

import com.duong.RestaurantManagement.auth.service.SessionAccess;
import com.duong.RestaurantManagement.dining.model.DiningSession;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.dining.repository.DiningSessionRepo;
import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.invoice.model.InvoiceStatus;
import com.duong.RestaurantManagement.invoice.repository.InvoiceRepo;
import com.duong.RestaurantManagement.invoice.service.InvoiceService;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalCaptureOrderResponse;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalAmount;
import com.duong.RestaurantManagement.order.exception.InvalidOrderStateException;
import com.duong.RestaurantManagement.order.service.OrderService;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalLink;
import com.duong.RestaurantManagement.payment.dto.paypal.PaypalOrderResponse;
import com.duong.RestaurantManagement.payment.model.Payment;
import com.duong.RestaurantManagement.payment.model.PaymentMethod;
import com.duong.RestaurantManagement.payment.model.PaymentStatus;
import com.duong.RestaurantManagement.payment.repository.PaymentRepository;
import com.duong.RestaurantManagement.payment.service.PaymentProviderException;
import com.duong.RestaurantManagement.payment.service.PaymentService;
import com.duong.RestaurantManagement.payment.service.PaypalOrderClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private final PaymentRepository payments = mock(PaymentRepository.class);
    private final InvoiceRepo invoices = mock(InvoiceRepo.class);
    private final DiningSessionRepo sessions = mock(DiningSessionRepo.class);
    private final SessionAccess access = mock(SessionAccess.class);
    private final OrderService orders = mock(OrderService.class);
    private final PaypalOrderClient paypal = mock(PaypalOrderClient.class);
    private final InvoiceService invoiceService = mock(InvoiceService.class);
    private final CountingTransactions transactions = new CountingTransactions();
    private final AtomicReference<Payment> reserved = new AtomicReference<>();
    private final DiningSession session = DiningSession.builder().diningSessionId(3L)
            .diningStatus(DiningStatus.WAITING_FOR_PAYMENT).build();
    private final Invoice invoice = Invoice.builder().invoiceId(7L).diningSession(session)
            .invoiceStatus(InvoiceStatus.UNPAID).totalPay(new BigDecimal("20.00")).build();
    private final PaypalOrderResponse providerOrder = new PaypalOrderResponse("ORDER-1", "PAYER_ACTION_REQUIRED",
            List.of(new PaypalLink("https://www.sandbox.paypal.com/checkoutnow?token=ORDER-1", "payer-action", "GET")));
    private PaymentService service;

    @BeforeEach
    void setup() {
        PaymentService target = new PaymentService(payments, invoices, sessions, access, orders, paypal,
                new TransactionTemplate(transactions), invoiceService);
        ProxyFactory proxy = new ProxyFactory(target);
        proxy.addAdvice(new TransactionInterceptor(transactions, new AnnotationTransactionAttributeSource()));
        service = (PaymentService) proxy.getProxy();
        when(invoices.findLockedById(7L)).thenAnswer(call -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            return Optional.of(invoice);
        });
        when(sessions.findLockedById(3L)).thenReturn(Optional.of(session));
        when(payments.findFirstByInvoice_InvoiceIdAndPaymentStatusInOrderByPaymentIdDesc(eq(7L), anyList()))
                .thenAnswer(call -> Optional.ofNullable(reserved.get()));
        when(payments.save(any(Payment.class))).thenAnswer(call -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            Payment payment = call.getArgument(0);
            payment.setPaymentId(1L);
            reserved.set(payment);
            return payment;
        });
        when(payments.findById(1L)).thenAnswer(call -> Optional.ofNullable(reserved.get()));
        when(paypal.createOrder(eq(7L), any(), anyString(), anyString())).thenAnswer(call -> {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertTrue(transactions.commits > 0, "Reservation must commit before the HTTP call");
            assertNotNull(reserved.get());
            return providerOrder;
        });
    }

    @Test
    void commitsReservationBeforePaypalAndPersistsCheckoutInAnotherTransaction() {
        var checkout = service.createPaypalOrder(7L);
        assertEquals("ORDER-1", checkout.providerOrderId());
        assertEquals(providerOrder.links().getFirst().href(), checkout.approvalUrl());
        assertEquals(PaymentStatus.PENDING, checkout.paymentStatus());
        Payment payment = reserved.get();
        assertEquals(new BigDecimal("20.00"), payment.getAmount());
        assertEquals("USD", payment.getCurrency());
        assertEquals(PaymentMethod.PAYPAL, payment.getPaymentMethod());
        assertEquals("ORDER-1", payment.getPaypalOrderId());
        assertEquals(checkout.approvalUrl(), payment.getPaypalApprovalUrl());
        assertDoesNotThrow(() -> UUID.fromString(payment.getPaypalCreateRequestId()));
        assertEquals(2, transactions.commits);
        verify(access).requireSession(3L);
    }

    @Test
    void reusesSavedCheckoutAfterCheckingAccessAgain() {
        var first = service.createPaypalOrder(7L);
        assertEquals(first, service.createPaypalOrder(7L));
        verify(paypal, times(1)).createOrder(anyLong(), any(), anyString(), anyString());
        verify(access, times(2)).requireSession(3L);
    }

    @Test
    void timeoutKeepsReservationAndRetryUsesSameRequestIdAmountAndCurrency() {
        AtomicReference<String> requestId = new AtomicReference<>();
        when(paypal.createOrder(eq(7L), any(), anyString(), anyString())).thenAnswer(call -> {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertEquals(1, transactions.commits);
            requestId.set(call.getArgument(3));
            throw new PaymentProviderException("timeout");
        });
        assertThrows(PaymentProviderException.class, () -> service.createPaypalOrder(7L));
        assertEquals(PaymentStatus.PENDING, reserved.get().getPaymentStatus());
        assertNull(reserved.get().getPaypalOrderId());
        doReturn(providerOrder).when(paypal).createOrder(7L, new BigDecimal("20.00"), "USD", requestId.get());
        service.createPaypalOrder(7L);
        assertEquals(requestId.get(), reserved.get().getPaypalCreateRequestId());
        verify(payments, times(2)).save(any()); // One reservation and one result, no new attempt on retry.
    }

    @Test
    void suspendsCallingTransactionDuringPaypalRequest() {
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            service.createPaypalOrder(7L);
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
        });
        assertEquals(3, transactions.commits);
    }

    @Test
    void deniesOtherSessionBeforeReservingOrCallingPaypal() {
        doThrow(new AccessDeniedException("Not your session")).when(access).requireSession(3L);
        assertThrows(AccessDeniedException.class, () -> service.createPaypalOrder(7L));
        verify(payments, never()).save(any());
        verifyNoInteractions(paypal);
    }

    @Test
    void rejectsClosedInvoicesUnfinishedOrdersAndInvalidAmounts() {
        for (InvoiceStatus status : List.of(InvoiceStatus.PAID, InvoiceStatus.CANCELLED)) {
            invoice.setInvoiceStatus(status);
            assertThrows(InvalidOrderStateException.class, () -> service.createPaypalOrder(7L));
        }
        invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
        session.setDiningStatus(DiningStatus.ACTIVE);
        assertThrows(InvalidOrderStateException.class, () -> service.createPaypalOrder(7L));
        session.setDiningStatus(DiningStatus.WAITING_FOR_PAYMENT);
        when(orders.hasActiveOrderByDiningSession(3L)).thenReturn(true);
        assertThrows(InvalidOrderStateException.class, () -> service.createPaypalOrder(7L));
        when(orders.hasActiveOrderByDiningSession(3L)).thenReturn(false);
        for (BigDecimal amount : new BigDecimal[]{null, BigDecimal.ZERO, BigDecimal.ONE.negate()}) {
            invoice.setTotalPay(amount);
            assertThrows(InvalidOrderStateException.class, () -> service.createPaypalOrder(7L));
        }
        verify(payments, never()).save(any());
        verifyNoInteractions(paypal);
    }

    @Test
    void refusesExpiredUncertainAttemptsAndAttemptsAlreadyCapturing() {
        when(paypal.createOrder(anyLong(), any(), anyString(), anyString())).thenThrow(new PaymentProviderException("timeout"));
        assertThrows(PaymentProviderException.class, () -> service.createPaypalOrder(7L));
        clearInvocations(paypal);
        reserved.get().setPaypalCreateRequestedAt(Instant.now().minus(6, ChronoUnit.HOURS));
        assertThrows(InvalidOrderStateException.class, () -> service.createPaypalOrder(7L));
        reserved.get().setPaypalCreateRequestedAt(Instant.now());
        reserved.get().setPaymentStatus(PaymentStatus.CAPTURING);
        assertThrows(InvalidOrderStateException.class, () -> service.createPaypalOrder(7L));
        verifyNoInteractions(paypal);
    }

    @Test
    void rejectsMalformedProviderResponseAndPreservesReservation() {
        when(paypal.createOrder(anyLong(), any(), anyString(), anyString()))
                .thenReturn(new PaypalOrderResponse("ORDER-1", "CREATED", List.of()));
        assertThrows(PaymentProviderException.class, () -> service.createPaypalOrder(7L));
        assertEquals(1, transactions.commits);
        assertNotNull(reserved.get().getPaypalCreateRequestId());
        assertEquals(PaymentStatus.PENDING, reserved.get().getPaymentStatus());
    }

    private void prepareCapture() {
        service.createPaypalOrder(7L);
        when(payments.findInvoiceIdByPaypalOrderId("ORDER-1")).thenReturn(Optional.of(7L));
        when(payments.findByPaypalOrderId("ORDER-1")).thenAnswer(call -> Optional.of(reserved.get()));
        when(paypal.capturePaypalOrder("ORDER-1")).thenAnswer(call -> {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertEquals(PaymentStatus.CAPTURING, reserved.get().getPaymentStatus());
            return captureResult("ORDER-1", "7", "COMPLETED", "USD", "20.00");
        });
    }

    private PaypalCaptureOrderResponse captureResult(String orderId, String reference, String status,
                                                     String currency, String amount) {
        return new PaypalCaptureOrderResponse(orderId, "COMPLETED", List.of(
                new PaypalCaptureOrderResponse.PurchaseUnit(reference, new PaypalCaptureOrderResponse.Payments(List.of(
                        new PaypalCaptureOrderResponse.Capture("CAPTURE-1", status, new PaypalAmount(currency, amount)))))));
    }

    @Test
    void completedCapturePaysInvoiceAndRetriesDoNotRepeatSideEffects() {
        prepareCapture();
        var table = com.duong.RestaurantManagement.table.model.RestaurantTable.builder()
                .restaurantTableStatus(false).build();
        session.setRestaurantTable(table);
        var diningService = new com.duong.RestaurantManagement.dining.service.DiningSessionServiceImp(
                null, sessions, null, null, null, null);
        var realInvoiceService = new com.duong.RestaurantManagement.invoice.service.InvoiceServiceImp(
                sessions, diningService, null, null, invoices, null, null, orders, payments);
        doAnswer(call -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            realInvoiceService.markInvoiceAsPaid(invoice);
            return null;
        }).when(invoiceService).markInvoiceAsPaid(invoice);

        var result = service.capturePaypalOrder("ORDER-1");
        assertEquals(PaymentStatus.COMPLETED, result.paymentStatus());
        assertNotNull(result.paidAt());
        assertEquals(InvoiceStatus.PAID, invoice.getInvoiceStatus());
        assertEquals(DiningStatus.COMPLETED, session.getDiningStatus());
        assertNotNull(session.getEndAt());
        assertTrue(table.isRestaurantTableStatus());
        assertEquals(result, service.capturePaypalOrder("ORDER-1"));
        verify(invoiceService).markInvoiceAsPaid(invoice);
        verify(paypal).capturePaypalOrder("ORDER-1");
    }

    @Test
    void uncertainCaptureCanBeRetriedWithoutCompletingInvoicePrematurely() {
        prepareCapture();
        doThrow(new PaymentProviderException("timeout"))
                .doReturn(captureResult("ORDER-1", "7", "COMPLETED", "USD", "20.00"))
                .when(paypal).capturePaypalOrder("ORDER-1");
        assertThrows(PaymentProviderException.class, () -> service.capturePaypalOrder("ORDER-1"));
        assertEquals(PaymentStatus.CAPTURING, reserved.get().getPaymentStatus());
        assertEquals(InvoiceStatus.UNPAID, invoice.getInvoiceStatus());
        assertNull(reserved.get().getPaidAt());
        verifyNoInteractions(invoiceService);
        assertEquals(PaymentStatus.COMPLETED, service.capturePaypalOrder("ORDER-1").paymentStatus());
    }

    @Test
    void rejectsUnconfirmedMismatchedOrMalformedCapture() {
        prepareCapture();
        for (var result : java.util.Arrays.asList(null,
                captureResult("OTHER", "7", "COMPLETED", "USD", "20.00"),
                captureResult("ORDER-1", "8", "COMPLETED", "USD", "20.00"),
                captureResult("ORDER-1", "7", "PENDING", "USD", "20.00"),
                captureResult("ORDER-1", "7", "COMPLETED", "AUD", "20.00"),
                captureResult("ORDER-1", "7", "COMPLETED", "USD", "19.99"),
                captureResult("ORDER-1", "7", "COMPLETED", "USD", "invalid"))) {
            doReturn(result).when(paypal).capturePaypalOrder("ORDER-1");
            assertThrows(PaymentProviderException.class, () -> service.capturePaypalOrder("ORDER-1"));
            assertEquals(InvoiceStatus.UNPAID, invoice.getInvoiceStatus());
            assertNull(reserved.get().getPaidAt());
        }
        verifyNoInteractions(invoiceService);
    }

    @Test
    void captureRejectsUnauthorizedOrInvalidStateBeforeCallingPaypal() {
        prepareCapture();
        doThrow(new AccessDeniedException("Not your session")).when(access).requireSession(3L);
        assertThrows(AccessDeniedException.class, () -> service.capturePaypalOrder("ORDER-1"));
        doNothing().when(access).requireSession(3L);
        invoice.setInvoiceStatus(InvoiceStatus.CANCELLED);
        assertThrows(InvalidOrderStateException.class, () -> service.capturePaypalOrder("ORDER-1"));
        invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
        when(orders.hasActiveOrderByDiningSession(3L)).thenReturn(true);
        assertThrows(InvalidOrderStateException.class, () -> service.capturePaypalOrder("ORDER-1"));
        verify(paypal, never()).capturePaypalOrder(anyString());
        verifyNoInteractions(invoiceService);
    }

    // Exercises Spring's real transaction interceptor and synchronization lifecycle
    // without requiring a database or making live PayPal requests.
    private static class CountingTransactions extends AbstractPlatformTransactionManager {
        int commits;
        @Override protected Object doGetTransaction() { return new Object(); }
        @Override protected boolean isExistingTransaction(Object transaction) {
            return TransactionSynchronizationManager.isActualTransactionActive();
        }
        @Override protected void doBegin(Object transaction, TransactionDefinition definition) {}
        @Override protected void doCommit(DefaultTransactionStatus status) { commits++; }
        @Override protected void doRollback(DefaultTransactionStatus status) {}
        @Override protected Object doSuspend(Object transaction) { return transaction; }
        @Override protected void doResume(Object transaction, Object resources) {}
    }
}
