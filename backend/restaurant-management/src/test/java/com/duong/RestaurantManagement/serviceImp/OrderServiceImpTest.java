package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.request.OrderItemDTO;
import com.duong.RestaurantManagement.model.DiningSession;
import com.duong.RestaurantManagement.model.Order;
import com.duong.RestaurantManagement.repo.OrderRepo;
import com.duong.RestaurantManagement.service.DiningSessionService;
import com.duong.RestaurantManagement.service.OrderItemService;
import com.duong.RestaurantManagement.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl Unit Test")
class OrderServiceImpTest {

    @Mock
    private  OrderRepo orderRepo;
    @Mock
    private  DiningSessionService diningSessionService;
    @Mock
    private  OrderItemService orderItemService;

    @InjectMocks
    private  OrderServiceImp orderServiceImp;

    @Nested
    @DisplayName("Create Order Test")
    class CreateOrderTest{

        @Test
        @DisplayName("Should create order success when dining session is active")
        void testCreateOrder(){

                // given
                Long diningSessionId = 1L;
                DiningSession diningSession = new DiningSession();

                OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 2);
                OrderItemDTO orderItemDTO2 = new OrderItemDTO(2L, 1);

                AddOrderDTO addOrderDTO = new AddOrderDTO(diningSessionId, List.of(orderItemDTO, orderItemDTO2));

                when(diningSessionService.validateDiningSessionActiveStatus(diningSessionId))
                        .thenReturn(diningSession);

                when(orderItemService.addListOfOrderItemAndGetTotalPrice(anyList(), any(Order.class)))
                        .thenReturn(45.0);

                // when
                orderServiceImp.addOrder(addOrderDTO);

                // then
                verify(diningSessionService).validateDiningSessionActiveStatus(diningSessionId);
                verify(orderItemService).addListOfOrderItemAndGetTotalPrice(anyList(), any(Order.class));
                verify(orderRepo, times(2)).save(any(Order.class));

                ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
                verify(orderRepo, times(2)).save(orderCaptor.capture());

                List<Order> savedOrders = orderCaptor.getAllValues();

                Order firstSavedOrder = savedOrders.get(0);
                Order secondSavedOrder = savedOrders.get(1);

                assertEquals(diningSession, firstSavedOrder.getDiningSession());
                assertEquals(diningSession, secondSavedOrder.getDiningSession());

                assertEquals(45.0, secondSavedOrder.getOrderPrice());
            }
    }
}