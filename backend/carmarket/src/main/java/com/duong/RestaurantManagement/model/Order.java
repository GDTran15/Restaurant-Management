package com.duong.RestaurantManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private LocalDateTime createdAt;

    private double orderPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(unique = true, nullable = false)
    private String orderNumber;


    @ManyToOne
    @JoinColumn(name = "dining_session_id")
    private DiningSession diningSession;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}
