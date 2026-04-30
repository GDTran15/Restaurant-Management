package com.duong.RestaurantManagement.dto.member.request;

import com.duong.RestaurantManagement.model.MembershipRank;

public record AddMembershipRequest(
        MembershipRank membershipRank,
        double minSpent,
        double discountRate
) {
}
