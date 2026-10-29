package com.duong.RestaurantManagement.order;

import com.duong.RestaurantManagement.order.dto.response.CreateNewOrderEvent;
import com.duong.RestaurantManagement.order.dto.response.OrderNewStatus;
import com.duong.RestaurantManagement.order.dto.response.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderRealTimeListener {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreate(CreateNewOrderEvent createNewOrderEvent){
    OrderNewStatus update = new OrderNewStatus(createNewOrderEvent.orderId(), "PENDING");
    simpMessageSendingOperations.convertAndSend("/topic/orders/", update);
    simpMessageSendingOperations.convertAndSend("/topic/orders/session/" + createNewOrderEvent.diningSessionId(), update);

  }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStatusChange(OrderStatusEvent event){
        OrderNewStatus update = new OrderNewStatus(event.orderId(), event.newStatus());
        simpMessageSendingOperations
                .convertAndSend("/topic/orders/status", update);

        simpMessageSendingOperations
                .convertAndSend("/topic/orders/session/" + event.diningSessionId(), update);

    }
}
