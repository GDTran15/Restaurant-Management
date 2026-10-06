package com.duong.RestaurantManagement.member.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.dto.request.AddMembershipRequest;
import com.duong.RestaurantManagement.member.model.MembershipRank;

public interface MembershipService {
    void addMembership(AddMembershipRequest addMembershipRequest);

    MembershipRank identifyMembershipRank(BigDecimal spent);
}
