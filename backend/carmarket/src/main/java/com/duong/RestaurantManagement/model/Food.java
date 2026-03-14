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
@Builder
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    private String foodName;

    private boolean isAvailable;

    private String description;

    private int quantity;

    private String foodImageUrl;

    private double price;

    @ManyToOne
    private Menu menu;

    @OneToMany(mappedBy = "food")
    private List<FoodCategoryMap> foodCategoryMaps;

    @OneToMany(mappedBy = "food")
    private List<OrderItem> orderItems;


}
