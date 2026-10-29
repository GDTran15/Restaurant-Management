package com.duong.RestaurantManagement;

import com.duong.RestaurantManagement.auth.service.JwtService;
import com.duong.RestaurantManagement.auth.service.MyUserDetailsService;
import com.duong.RestaurantManagement.order.OrderRealTimeListener;
import com.duong.RestaurantManagement.order.OrderSocketSecurity;
import com.duong.RestaurantManagement.order.dto.response.OrderNewStatus;
import com.duong.RestaurantManagement.order.dto.response.OrderStatusEvent;
import com.duong.RestaurantManagement.order.dto.response.CreateNewOrderEvent;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderSocketSecurityTest {
    @Test
    void authenticatesAndRestrictsSubscriptionsAndPublishing() {
        var jwt = new JwtService();
        ReflectionTestUtils.setField(jwt, "secretKey", "dGVzdC1vbmx5LXNpZ25pbmcta2V5LTMyaHl0ZXMtbWluaW11bS1sb25n");
        ReflectionTestUtils.setField(jwt, "jwtExpiration", 60000L);
        ReflectionTestUtils.setField(jwt, "refreshExpiration", 60000L);
        var users = mock(MyUserDetailsService.class);
        var security = new OrderSocketSecurity(jwt, users);
        var connect = StompHeaderAccessor.create(StompCommand.CONNECT);
        assertThrows(AccessDeniedException.class, () -> send(security, connect));
        connect.setNativeHeader("Authorization", "Bearer " + jwt.generateDiningToken(7L));
        send(security, connect);
        assertEquals("7", connect.getUser().getName());
        var subscribe = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        subscribe.setUser(connect.getUser());
        subscribe.setDestination("/topic/orders/session/7");
        assertDoesNotThrow(() -> send(security, subscribe));
        subscribe.setDestination("/topic/orders/session/8");
        assertThrows(AccessDeniedException.class, () -> send(security, subscribe));
        subscribe.setDestination("/topic/orders");
        assertThrows(AccessDeniedException.class, () -> send(security, subscribe));
        for (String destination : new String[]{"/topic/orders/status", "/topic/orders/", "/topic/orders/session/*", "/topic/orders/session/7/**"}) {
            subscribe.setDestination(destination);
            assertThrows(AccessDeniedException.class, () -> send(security, subscribe));
        }
        assertThrows(AccessDeniedException.class, () -> send(security, StompHeaderAccessor.create(StompCommand.SEND)));

        var admin = User.withUsername("admin").password("unused").roles("ADMIN").build();
        when(users.loadUserByUsername("admin")).thenReturn(admin);
        var staffConnect = StompHeaderAccessor.create(StompCommand.CONNECT);
        staffConnect.setNativeHeader("Authorization", "Bearer " + jwt.generateToken(admin));
        send(security, staffConnect);
        subscribe.setUser(staffConnect.getUser());
        subscribe.setDestination("/topic/orders/status");
        assertDoesNotThrow(() -> send(security, subscribe));
        subscribe.setDestination("/topic/orders/");
        assertDoesNotThrow(() -> send(security, subscribe));
        for (String destination : new String[]{"/topic/**", "/topic/orders/session/8", "/queue/private", "/app/orders"}) {
            subscribe.setDestination(destination);
            assertThrows(AccessDeniedException.class, () -> send(security, subscribe));
        }
        var publish = StompHeaderAccessor.create(StompCommand.SEND);
        publish.setUser(staffConnect.getUser());
        publish.setDestination("/topic/orders/status");
        assertThrows(AccessDeniedException.class, () -> send(security, publish));
        var anonymousSubscribe = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        anonymousSubscribe.setDestination("/topic/orders/status");
        assertThrows(AccessDeniedException.class, () -> send(security, anonymousSubscribe));

        var manager = User.withUsername("manager").password("unused").roles("MANAGER").build();
        when(users.loadUserByUsername("manager")).thenReturn(manager);
        staffConnect.setNativeHeader("Authorization", "Bearer " + jwt.generateToken(manager));
        assertDoesNotThrow(() -> send(security, staffConnect));
        subscribe.setUser(staffConnect.getUser());
        subscribe.setDestination("/topic/orders/status");
        assertDoesNotThrow(() -> send(security, subscribe));

        var refreshConnect = StompHeaderAccessor.create(StompCommand.CONNECT);
        refreshConnect.setNativeHeader("Authorization", "Bearer " + jwt.generateRefreshToken(admin));
        assertThrows(AccessDeniedException.class, () -> send(security, refreshConnect));
        refreshConnect.setNativeHeader("Authorization", "Bearer invalid.jwt.signature");
        assertThrows(AccessDeniedException.class, () -> send(security, refreshConnect));
        ReflectionTestUtils.setField(jwt, "jwtExpiration", -60000L);
        refreshConnect.setNativeHeader("Authorization", "Bearer " + jwt.generateToken(admin));
        assertThrows(AccessDeniedException.class, () -> send(security, refreshConnect));

        var disabled = User.withUsername("admin").password("unused").roles("ADMIN").disabled(true).build();
        when(users.loadUserByUsername("admin")).thenReturn(disabled);
        ReflectionTestUtils.setField(jwt, "jwtExpiration", 60000L);
        refreshConnect.setNativeHeader("Authorization", "Bearer " + jwt.generateToken(disabled));
        assertThrows(AccessDeniedException.class, () -> send(security, refreshConnect));
    }

    private void send(OrderSocketSecurity security, StompHeaderAccessor accessor) {
        accessor.setLeaveMutable(true);
        security.preSend(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()), null);
    }

    @Test
    void broadcastsCreationToStaffAndOnlyTheOwningSession() {
        var broker = mock(SimpMessageSendingOperations.class);
        new OrderRealTimeListener(broker).handleOrderCreate(new CreateNewOrderEvent(1L, 7L));
        var update = new OrderNewStatus(1L, "PENDING");
        verify(broker).convertAndSend("/topic/orders/", update);
        verify(broker).convertAndSend("/topic/orders/session/7", update);
        verifyNoMoreInteractions(broker);
    }

    @Test
    void broadcastsStatusToStaffAndOnlyTheOwningSession() {
        var broker = mock(SimpMessageSendingOperations.class);
        new OrderRealTimeListener(broker).handleStatusChange(new OrderStatusEvent(1L, "PENDING", "IN_PROGRESS", 7L));
        var update = new OrderNewStatus(1L, "IN_PROGRESS");
        verify(broker).convertAndSend("/topic/orders/status", update);
        verify(broker).convertAndSend("/topic/orders/session/7", update);
        verifyNoMoreInteractions(broker);
    }
}
