package com.duong.RestaurantManagement.member.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.dto.request.AddMemberRequest;
import com.duong.RestaurantManagement.member.dto.response.GetMemberInfoDTO;
import com.duong.RestaurantManagement.member.model.Member;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

public interface MemberService {
    void addMember(@Valid AddMemberRequest addMemberRequest);

    void updateMemberAfterPayment(Member member, BigDecimal totalPay);

     GetMemberInfoDTO getMemberByPhoneNumber(String phoneNumber);
}
