package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderForAdminDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderNumberForEachStatus;
import com.duong.RestaurantManagement.model.Order;
import com.duong.RestaurantManagement.model.OrderStatus;
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
}
