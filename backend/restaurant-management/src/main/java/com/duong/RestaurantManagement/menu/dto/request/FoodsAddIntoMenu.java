package com.duong.RestaurantManagement.menu.dto.request;
import jakarta.validation.constraints.*;

import java.util.List;

public record FoodsAddIntoMenu(
        @NotEmpty List<@NotNull @Positive Long> foodIdList
) {
}
