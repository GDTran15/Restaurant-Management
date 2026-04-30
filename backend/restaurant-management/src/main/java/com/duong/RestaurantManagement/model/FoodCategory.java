package com.duong.RestaurantManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
