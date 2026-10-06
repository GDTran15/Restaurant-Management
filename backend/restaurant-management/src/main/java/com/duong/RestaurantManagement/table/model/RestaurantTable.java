package com.duong.RestaurantManagement.table.model;

import com.duong.RestaurantManagement.dining.model.DiningSession;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@AllArgsConstructor
@Getter
@Setter
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
