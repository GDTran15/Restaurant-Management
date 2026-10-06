package com.duong.RestaurantManagement;

import com.duong.RestaurantManagement.auth.service.JwtService;
import com.duong.RestaurantManagement.dining.service.DiningSessionService;
import com.duong.RestaurantManagement.food.dto.request.UpdateFoodRequestDTO;
import com.duong.RestaurantManagement.food.service.FoodService;
import com.duong.RestaurantManagement.invoice.service.InvoiceService;
import com.duong.RestaurantManagement.menu.service.MenuService;
import com.duong.RestaurantManagement.order.dto.request.*;
import com.duong.RestaurantManagement.order.service.OrderService;
import com.duong.RestaurantManagement.payment.dto.request.CashPaymentRequest;
import com.duong.RestaurantManagement.payment.service.PaymentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.User;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "TEST_DATABASE_URL", matches = ".+")
class BackendIntegrationTest {
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    com.paypal.sdk.PaypalServerSdkClient paypal;
    @org.springframework.test.context.bean.override.mockito.MockitoSpyBean
    InvoiceService invoiceSpy;
    @Autowired JdbcTemplate db;
    @Autowired DiningSessionService dining;
    @Autowired OrderService orders;
    @Autowired InvoiceService invoices;
    @Autowired PaymentService payments;
    @Autowired MenuService menus;
    @Autowired FoodService foods;
    @Autowired JwtService jwt;
    @Autowired MockMvc http;

    @BeforeEach
    void seed() {
        db.execute("TRUNCATE payments, invoices, members, memberships, order_items, orders, dining_sessions, restaurant_tables, menu_items, menus, food_category_map, food_category, foods, users RESTART IDENTITY CASCADE");
        db.update("INSERT INTO restaurant_tables VALUES (1,1,4,'test-qr-token',true)");
        db.update("INSERT INTO foods VALUES (1,'Soup',true,'Soup',10,'image',0.10)");
        db.update("INSERT INTO food_category VALUES (1,'First'),(2,'Second')");
        db.update("INSERT INTO food_category_map(food_id,food_category_id) VALUES (1,1)");
        db.update("INSERT INTO menus VALUES (1,'Lunch','Lunch',true),(2,'Dinner','Dinner',false)");
        db.update("INSERT INTO menu_items(food_id,menu_id) VALUES (1,1)");
        db.update("INSERT INTO users(username,password,email,phone,role) VALUES ('admin',?,'admin@test.local',12345,'ADMIN')",
                new BCryptPasswordEncoder(12).encode("password123"));
    }

    private Long session() { return dining.getDiningSession("test-qr-token").dinningSessionId(); }
    private Long order(Long session, int quantity) {
        orders.addOrder(new AddOrderDTO(session, List.of(new OrderItemDTO(1, quantity))));
        return db.queryForObject("SELECT max(order_id) FROM orders", Long.class);
    }
    private Long checkout() {
        Long order = order(session(), 3);
        orders.orderStartToProcess(order);
        orders.completeOrder(order);
        return invoices.createNewInvoice(1L).invoiceId();
    }
    private String staffToken() { return jwt.generateToken(User.withUsername("admin").password("unused").roles("ADMIN").build()); }

    @Test
    void flywayAndQueriesLoad() {
        assertEquals("2", db.queryForObject("SELECT version FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1", String.class));
        assertEquals(2, menus.getMenuLists().size());
        assertEquals(1, menus.getFoodOfMenu(1L,0,10,"").getTotalElements());
        assertEquals(1, foods.getFoodList(0,10,"").getTotalElements());
        assertNotNull(menus.getMenuActiveWithItems(0,10));
        assertNotNull(orders.getNumberOfOrderForEachStatus());
        assertNotNull(invoices.getTodayInvoice());
    }

    @Test
    void loginRefreshAndRoleProtection() throws Exception {
        http.perform(post("/authenticate").contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("accessToken").exists());
        http.perform(post("/authenticate").contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
        http.perform(post("/refresh-token")).andExpect(status().isUnauthorized());
        http.perform(get("/orders/pending").header("Authorization","Bearer invalid")).andExpect(status().isUnauthorized());
        var user = User.withUsername("admin").password("unused").roles("ADMIN").build();
        http.perform(get("/orders/pending").header("Authorization","Bearer " + jwt.generateRefreshToken(user)))
                .andExpect(status().isUnauthorized());
        http.perform(post("/refresh-token").cookie(new Cookie("refreshToken", jwt.generateToken(user))))
                .andExpect(status().isUnauthorized());
        http.perform(post("/refresh-token").cookie(new Cookie("refreshToken", jwt.generateRefreshToken(user))))
                .andExpect(status().isOk()).andExpect(jsonPath("accessToken").exists());
        http.perform(get("/orders/pending").header("Authorization","Bearer " + staffToken())).andExpect(status().isOk());
        http.perform(post("/payments/cash").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerSessionIsolationAndValidation() throws Exception {
        var session = dining.getDiningSession("test-qr-token");
        String auth = "Bearer " + session.accessToken();
        http.perform(get("/orders").param("diningSessionId",session.dinningSessionId().toString()).header("Authorization",auth))
                .andExpect(status().isOk());
        http.perform(get("/orders").param("diningSessionId","999").header("Authorization",auth)).andExpect(status().isForbidden());
        http.perform(post("/payments/cash").header("Authorization",auth).contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        http.perform(post("/orders").header("Authorization",auth).contentType("application/json")
                .content("{\"diningSessionId\":" + session.dinningSessionId() + ",\"orderItemDTOS\":[{\"foodId\":1,\"quantity\":-1}]}"))
                .andExpect(status().isBadRequest());
        assertEquals(10, db.queryForObject("SELECT quantity FROM foods WHERE food_id=1",Integer.class));
    }

    @Test
    void decimalCheckoutFreezesOrderingAndCashClosesSession() {
        Long invoice = checkout();
        Long session = session();
        assertEquals(new BigDecimal("0.30"), invoices.getInvoice(invoice).totalPay());
        assertThrows(RuntimeException.class, () -> order(session,1));
        assertEquals(invoice,invoices.createNewInvoice(1L).invoiceId());
        payments.createCashPayment(new CashPaymentRequest(invoice,new BigDecimal("1.00")));
        assertThrows(RuntimeException.class, () -> payments.createCashPayment(new CashPaymentRequest(invoice,new BigDecimal("1.00"))));
        assertEquals("COMPLETED",db.queryForObject("SELECT dining_status FROM dining_sessions WHERE dining_session_id=?",String.class,session));
        assertEquals(Boolean.TRUE,db.queryForObject("SELECT restaurant_table_status FROM restaurant_tables WHERE restaurant_table_id=1",Boolean.class));
        assertEquals(new BigDecimal("0.70"),db.queryForObject("SELECT cash_change FROM payments",BigDecimal.class));
    }

    @Test
    void cancellationRestoresStockOnceAndRollbackPreservesStock() {
        Long session = session();
        Long order = order(session,2);
        orders.cancelOrder(order);
        assertThrows(RuntimeException.class, () -> orders.cancelOrder(order));
        assertEquals(10,db.queryForObject("SELECT quantity FROM foods WHERE food_id=1",Integer.class));
        assertThrows(RuntimeException.class, () -> orders.addOrder(new AddOrderDTO(session,List.of(new OrderItemDTO(1,2),new OrderItemDTO(999,1)))));
        assertEquals(10,db.queryForObject("SELECT quantity FROM foods WHERE food_id=1",Integer.class));
        assertEquals(1,db.queryForObject("SELECT count(*) FROM orders",Integer.class));
    }

    @Test
    void offMenuAndUnavailableFoodsRejected() {
        Long session = session();
        db.update("UPDATE foods SET is_available=false WHERE food_id=1");
        assertThrows(RuntimeException.class, () -> order(session,1));
        db.update("UPDATE foods SET is_available=true WHERE food_id=1");
        db.update("DELETE FROM menu_items");
        assertThrows(RuntimeException.class, () -> order(session,1));
        assertEquals(10,db.queryForObject("SELECT quantity FROM foods WHERE food_id=1",Integer.class));
    }

    @Test
    void categoryEditAndHistoricalPrices() {
        Long order = order(session(),3);
        orders.orderStartToProcess(order); orders.completeOrder(order);
        var edit = new UpdateFoodRequestDTO("Soup",true,"Soup",10,"image",new BigDecimal("1.20"),2L);
        foods.updateFoodInformation(edit,1L); foods.updateFoodInformation(edit,1L);
        assertEquals(1,db.queryForObject("SELECT count(*) FROM food_category_map",Integer.class));
        var item = dining.getDiningSessionOrderItems(session()).getFirst();
        assertEquals(new BigDecimal("0.10"),item.foodPrice());
        assertEquals(new BigDecimal("0.30"),item.itemPrice());
    }

    @Test
    void allMenuPathsEnforceActivationRules() {
        assertThrows(RuntimeException.class, () -> menus.changeMenuActivation(2L,true));
        session();
        assertThrows(RuntimeException.class, () -> menus.changeMenuActivation(1L,false));
    }

    private void pendingPayment(Long invoice) {
        db.update("INSERT INTO payments(amount,paypal_order_id,payment_status,payment_method,invoice_id,currency) VALUES (0.30,'paypal-test','PENDING','PAYPAL',?,'USD')",invoice);
    }

    private com.paypal.sdk.models.Order captured(String amount, String currency) {
        return new com.paypal.sdk.models.Order.Builder().id("paypal-test").status(com.paypal.sdk.models.OrderStatus.COMPLETED)
                .purchaseUnits(List.of(new com.paypal.sdk.models.PurchaseUnit.Builder()
                        .payments(new com.paypal.sdk.models.PaymentCollection.Builder().captures(List.of(
                                new com.paypal.sdk.models.OrdersCapture.Builder().status(com.paypal.sdk.models.CaptureStatus.COMPLETED)
                                        .amount(new com.paypal.sdk.models.Money.Builder(currency,amount).build()).build())).build()).build())).build();
    }

    @SuppressWarnings("unchecked")
    private com.paypal.sdk.http.response.ApiResponse<com.paypal.sdk.models.Order> response(com.paypal.sdk.models.Order order) {
        var response = (com.paypal.sdk.http.response.ApiResponse<com.paypal.sdk.models.Order>) org.mockito.Mockito.mock(com.paypal.sdk.http.response.ApiResponse.class);
        org.mockito.Mockito.when(response.getResult()).thenReturn(order);
        return response;
    }

    @Test
    void cashInvalidatesPendingPaypalBeforeAnyExternalCapture() {
        Long invoice=checkout(); pendingPayment(invoice);
        payments.createCashPayment(new CashPaymentRequest(invoice,new BigDecimal("1.00")));
        assertThrows(RuntimeException.class,()->payments.captureOrder("paypal-test"));
        org.mockito.Mockito.verifyNoInteractions(paypal);
        assertEquals("CANCELLED",db.queryForObject("SELECT payment_status FROM payments WHERE paypal_order_id='paypal-test'",String.class));
    }

    @Test
    void captureRecoveryAfterLocalFailureDoesNotChargeAgain() throws Exception {
        Long invoice=checkout(); pendingPayment(invoice);
        var controller=org.mockito.Mockito.mock(com.paypal.sdk.controllers.OrdersController.class);
        org.mockito.Mockito.when(paypal.getOrdersController()).thenReturn(controller);
        org.mockito.Mockito.when(controller.getOrder(org.mockito.ArgumentMatchers.any())).thenReturn(response(captured("0.30","USD")));
        org.mockito.Mockito.doThrow(new IllegalStateException("simulate local persistence failure")).doCallRealMethod()
                .when(invoiceSpy).markInvoiceAsPaid(org.mockito.ArgumentMatchers.any());
        assertThrows(IllegalStateException.class,()->payments.captureOrder("paypal-test"));
        assertEquals("CAPTURING",db.queryForObject("SELECT payment_status FROM payments WHERE paypal_order_id='paypal-test'",String.class));
        assertThrows(RuntimeException.class,()->payments.createCashPayment(new CashPaymentRequest(invoice,new BigDecimal("1.00"))));
        payments.captureOrder("paypal-test"); payments.captureOrder("paypal-test");
        assertEquals("PAID",db.queryForObject("SELECT invoice_status FROM invoices WHERE invoice_id=?",String.class,invoice));
        assertEquals(1,db.queryForObject("SELECT count(*) FROM payments",Integer.class));
        org.mockito.Mockito.verify(controller,org.mockito.Mockito.never()).captureOrder(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void captureUsesIdempotencyKeyAndVerifiesMoney() throws Exception {
        Long invoice=checkout(); pendingPayment(invoice);
        var controller=org.mockito.Mockito.mock(com.paypal.sdk.controllers.OrdersController.class);
        org.mockito.Mockito.when(paypal.getOrdersController()).thenReturn(controller);
        org.mockito.Mockito.when(controller.getOrder(org.mockito.ArgumentMatchers.any())).thenReturn(response(
                new com.paypal.sdk.models.Order.Builder().id("paypal-test").status(com.paypal.sdk.models.OrderStatus.APPROVED).build()));
        org.mockito.Mockito.when(controller.captureOrder(org.mockito.ArgumentMatchers.any())).thenReturn(response(captured("0.30","EUR")));
        assertThrows(RuntimeException.class,()->payments.captureOrder("paypal-test"));
        assertEquals("UNPAID",db.queryForObject("SELECT invoice_status FROM invoices WHERE invoice_id=?",String.class,invoice));
        assertThrows(RuntimeException.class,()->invoices.cancelInvoice(invoice));
        var argument=org.mockito.ArgumentCaptor.forClass(com.paypal.sdk.models.CaptureOrderInput.class);
        org.mockito.Mockito.verify(controller).captureOrder(argument.capture());
        assertEquals("capture-1",argument.getValue().getPaypalRequestId());
        org.mockito.Mockito.when(controller.getOrder(org.mockito.ArgumentMatchers.any())).thenReturn(response(captured("0.31","USD")));
        assertThrows(RuntimeException.class,()->payments.captureOrder("paypal-test"));
    }

    @Test
    void concurrentScansCancellationAndCashAreSerialized() throws Exception {
        try (var pool=Executors.newFixedThreadPool(2)) {
            var scans=pool.<Long>invokeAll(List.of(this::session,this::session));
            Long session=scans.get(0).get();
            assertEquals(session,scans.get(1).get());
            Long order=order(session,2);
            Callable<Boolean> cancel=()->{try {orders.cancelOrder(order);return true;}catch(RuntimeException ex){return false;}};
            var cancellations=pool.invokeAll(List.of(cancel,cancel));
            assertNotEquals(cancellations.get(0).get(),cancellations.get(1).get());
            assertEquals(10,db.queryForObject("SELECT quantity FROM foods WHERE food_id=1",Integer.class));
            Long invoice=checkout();
            Callable<Boolean> cash=()->{try {payments.createCashPayment(new CashPaymentRequest(invoice,new BigDecimal("1.00")));return true;}catch(RuntimeException ex){return false;}};
            var settlements=pool.invokeAll(List.of(cash,cash));
            assertNotEquals(settlements.get(0).get(),settlements.get(1).get());
            assertEquals(1,db.queryForObject("SELECT count(*) FROM payments WHERE payment_status='COMPLETED'",Integer.class));
        }
    }
}
