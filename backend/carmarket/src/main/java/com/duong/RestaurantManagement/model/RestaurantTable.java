package com.duong.RestaurantManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int restaurantTableId;

    private int restaurantTableNumber;

    private int capacity;


    private boolean restaurantTableStatus;

    @OneToMany(mappedBy = "restaurantTable")
    private List<DiningSession> diningSessions;
}
