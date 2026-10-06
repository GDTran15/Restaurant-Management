package com.duong.RestaurantManagement.member.dto.response;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.model.MembershipRank;

public record GetMemberInfoDTO(
        String firstName,
        String lastName,
        String memberPhone,
        String memberEmail,
        MembershipRank membershipRank,
        BigDecimal totalSpent
) {
}
