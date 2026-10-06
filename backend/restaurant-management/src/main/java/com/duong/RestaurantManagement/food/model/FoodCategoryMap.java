package com.duong.RestaurantManagement.food.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table(name = "food_category_map")
public class FoodCategoryMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodCategoryMapId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false, unique = true)
    private Food food;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_category_id", nullable = false)
    private FoodCategory foodCategory;
}
