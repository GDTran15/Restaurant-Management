package com.duong.RestaurantManagement.mapper;

import com.duong.RestaurantManagement.dto.order.request.OrderItemDTO;
import com.duong.RestaurantManagement.dto.order.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderForAdminDTO;
import com.duong.RestaurantManagement.dto.order.response.GetOrderItemDTO;
import com.duong.RestaurantManagement.model.Order;
import com.duong.RestaurantManagement.model.OrderItem;

import java.util.List;



public class OrderMapper {

    public static GetCustomerOrderDTO toCustomerDTO(Order order) {
        return new GetCustomerOrderDTO(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getOrderStatus(),
                order.getOrderPrice(),
                toOrderItemDTOList(order.getOrderItems())
        );
    }

    public static GetOrderForAdminDTO orderToGetOrderForAdminDTO(Order order) {
        return  new GetOrderForAdminDTO(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getOrderStatus(),
                order.getOrderPrice(),
                order.getDiningSession().getRestaurantTable().getRestaurantTableId(),
                order.getDiningSession().getRestaurantTable().getRestaurantTableNumber(),
                order.getDiningSession().getDiningSessionId(),
               toOrderItemDTOList(order.getOrderItems()));
    }

        public static List<GetOrderItemDTO> toOrderItemDTOList (List<OrderItem> orderItems) {
            return orderItems.stream()
                    .map(OrderMapper::toOrderItemDTO)
                    .toList();
        }

        public static GetOrderItemDTO toOrderItemDTO (OrderItem orderItem){
            return new GetOrderItemDTO(
                    orderItem.getFood().getFoodName(),
                    orderItem.getQuantity(),
                    orderItem.getTotalPrice()
            );
        }

}