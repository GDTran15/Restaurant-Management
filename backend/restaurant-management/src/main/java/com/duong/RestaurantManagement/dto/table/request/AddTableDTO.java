package com.duong.RestaurantManagement.dto.table.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddTableDTO(
        @NotNull(message = "Please enter table number")
        @Min(value = 1,message = "Table number cannot smaller than 1")
        Integer tableNumber,
        @NotNull(message = "Please enter table capacity")
        @Min(value = 2, message = "Table capacity cannot smaller than 2")
        Integer capacity
) {
}
