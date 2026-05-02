package com.duong.RestaurantManagement.dto.member.request;

import com.duong.RestaurantManagement.model.MembershipRank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest (
        @NotBlank(message = "Please enter member first name")
        String firstName,
        @NotBlank(message = "Please enter member last name")
        String lastName,
        @NotBlank(message = "Please enter member phone number")
        String memberPhone,
        @NotBlank(message = "Please enter member email")
        String memberEmail,
        @NotNull(message = "Please enter member spent")
        Double totalSpent
) {
}
