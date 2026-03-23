package com.duong.RestaurantManagement.service;


import com.duong.RestaurantManagement.dto.order.request.AddOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.model.OrderStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface OrderService {
    void addOrder(AddOrderDTO addOrderDTO);

    void updateOrderStatus(Long orderId, OrderStatus orderStatus);

    void deleteOrder(Long orderId);

     List<GetCustomerOrderDTO> getDiningSessionOrder(Long diningSessionId);
}
