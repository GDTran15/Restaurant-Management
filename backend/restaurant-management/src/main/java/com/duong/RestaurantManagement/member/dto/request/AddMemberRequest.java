package com.duong.RestaurantManagement.member.dto.request;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.model.MembershipRank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest (
        @NotBlank(message = "Please enter member first name")
        String firstName,
        @NotBlank(message = "Please enter member last name")
        String lastName,
        @NotBlank(message = "Please enter member phone number")
        @Pattern(regexp = "^\\+?[0-9]{8,15}$") String memberPhone,
        @NotBlank(message = "Please enter member email")
        @Email String memberEmail,
        @NotNull(message = "Please enter member spent")
        @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal totalSpent
) {
}
