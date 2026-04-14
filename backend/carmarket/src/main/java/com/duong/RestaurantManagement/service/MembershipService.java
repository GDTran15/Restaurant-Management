package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.member.request.AddMembershipRequest;
import com.duong.RestaurantManagement.model.MembershipRank;

public interface MembershipService {
    void addMembership(AddMembershipRequest addMembershipRequest);

    MembershipRank identifyMembershipRank(double spent);
}
