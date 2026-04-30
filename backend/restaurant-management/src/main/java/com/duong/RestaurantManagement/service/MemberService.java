package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.member.request.AddMemberRequest;
import com.duong.RestaurantManagement.model.Member;
import jakarta.validation.Valid;

public interface MemberService {
    void addMember(@Valid AddMemberRequest addMemberRequest);

    void updateMemberAfterPayment(Member member, double totalPay);

}
