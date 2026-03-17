package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.food.request.AddFoodRequestDTO;
import com.duong.RestaurantManagement.repo.FoodRepo;
import com.duong.RestaurantManagement.serviceImp.FoodCategoryMapService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Food Service Unit Tests")
class FoodServiceTest {
    @Mock
    private FoodRepo foodRepo;
    @Mock
    private FoodCategoryMapService foodCategoryMapService;

    @InjectMocks
    private com.duong.RestaurantManagement.service.FoodService foodService; // class to test

    @Nested
    @DisplayName("Create Food Test")
    class CreateFoodTests{

        @Test
        @DisplayName("Should create food successfully when category exist")
        public void shouldCreateFoodSuccessfully() {
             //Given
            AddFoodRequestDTO dto = new AddFoodRequestDTO(
                    "Burger",
                    true,
                    "Delicious beef burger",
                    10,
                    "http://image.url/burger.jpg",
                    12.5,
                    1L
            );
            //When
            foodService.createNewFood(dto);
            //Then
        }
    }
}