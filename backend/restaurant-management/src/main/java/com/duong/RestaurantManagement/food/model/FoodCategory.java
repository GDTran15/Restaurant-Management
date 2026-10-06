package com.duong.RestaurantManagement.food.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "food_category")
public class FoodCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodCategoryId;

    @NotBlank(message = "Category name cannot be blank")
    private String foodCategoryName;

    @OneToMany(mappedBy = "foodCategory")
    private List<FoodCategoryMap> foodCategoryMaps;

}
