package com.duong.RestaurantManagement.member.dto.request;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.model.MembershipRank;

public record AddMembershipRequest(
        @NotNull MembershipRank membershipRank,
        @NotNull @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal minSpent,
        @NotNull @DecimalMin("0") @DecimalMax("1") @Digits(integer = 1, fraction = 6) BigDecimal discountRate
) {
}
