package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.member.request.AddMemberRequest;
import jakarta.validation.Valid;

public interface MemberService {
    void addMember(@Valid AddMemberRequest addMemberRequest);
}
