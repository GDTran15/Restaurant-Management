package com.duong.RestaurantManagement.food.model;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.menu.model.MenuItem;
import com.duong.RestaurantManagement.order.model.OrderItem;

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

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;



    @OneToOne(mappedBy = "food", fetch = FetchType.LAZY)
    private FoodCategoryMap foodCategoryMap;

    @OneToMany(mappedBy = "food")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "food")
    private List<MenuItem> menuItems;
}
