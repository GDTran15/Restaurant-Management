package com.duong.RestaurantManagement.dining.model;

import com.duong.RestaurantManagement.invoice.model.Invoice;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.table.model.RestaurantTable;


import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "dining_sessions")
@Builder
public class DiningSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diningSessionId;

    private LocalDateTime startAt;


    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private DiningStatus diningStatus;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_table_id", nullable = false)
    private RestaurantTable restaurantTable;

    @OneToMany(mappedBy = "diningSession")
    private List<Order> orders;

    @OneToMany(mappedBy = "diningSession")
    private List<Invoice> invoices;
}
