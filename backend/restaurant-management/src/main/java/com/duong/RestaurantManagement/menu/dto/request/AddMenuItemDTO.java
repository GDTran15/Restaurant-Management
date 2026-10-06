package com.duong.RestaurantManagement.menu.dto.request;
import jakarta.validation.constraints.*;

import java.util.List;

public record AddMenuItemDTO(
        @NotNull @Positive Long menuId,
        @NotEmpty List<@NotNull @Positive Long> foodIdList
) {
}
