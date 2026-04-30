package com.duong.RestaurantManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "restaurant_tables")
@Builder
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long restaurantTableId;

    private int restaurantTableNumber;

    private int capacity;

    private String tableQrCodeValue;


    private boolean restaurantTableStatus;

    @OneToMany(mappedBy = "restaurantTable")
    private List<DiningSession> diningSessions;
}
