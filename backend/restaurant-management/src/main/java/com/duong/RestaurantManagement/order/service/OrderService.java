package com.duong.RestaurantManagement.order.service;


import com.duong.RestaurantManagement.order.dto.request.AddOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderForAdminDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderNumberForEachStatus;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.order.model.OrderStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface OrderService {
    void addOrder(AddOrderDTO addOrderDTO);

    void completeOrder(Long orderId);

    void cancelOrder(Long orderId);

     List<GetCustomerOrderDTO> getDiningSessionOrder(Long diningSessionId);

    void orderStartToProcess(Long orderId);

    List<GetOrderForAdminDTO> getActiveDiningSessionOrder();

    List<GetOrderNumberForEachStatus> getNumberOfOrderForEachStatus();

    List<GetOrderForAdminDTO> getOrderByStatus(OrderStatus orderStatus);

    List<Order> getOrderFromDiningSession(Long diningSessionId);

     boolean hasActiveOrderByDiningSession(Long diningSessionId);
}
