package com.duong.RestaurantManagement.order.model;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.dining.model.DiningSession;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private LocalDateTime createdAt;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal orderPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(unique = true, nullable = false)
    private String orderNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dining_session_id", nullable = false)
    private DiningSession diningSession;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}
