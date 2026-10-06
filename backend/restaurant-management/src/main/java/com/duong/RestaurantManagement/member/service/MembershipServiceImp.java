package com.duong.RestaurantManagement.member.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.dto.request.AddMembershipRequest;
import com.duong.RestaurantManagement.member.model.Membership;
import com.duong.RestaurantManagement.member.model.MembershipRank;
import com.duong.RestaurantManagement.member.repository.MembershipRepo;
import com.duong.RestaurantManagement.member.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class MembershipServiceImp implements MembershipService {

    private final MembershipRepo membershipRepo;


    @Override
    public void addMembership(AddMembershipRequest addMembershipRequest) {
        Membership membership = Membership.builder()
                .membershipRank(addMembershipRequest.membershipRank())
                .minSpent(addMembershipRequest.minSpent())
                .discountRate(addMembershipRequest.discountRate())
                .build();
        membershipRepo.save(membership);
    }

    @Override
    public MembershipRank identifyMembershipRank(BigDecimal spent) {
        return membershipRepo.findAllByOrderByMinSpentDesc()
                .stream()
                .filter(membership -> spent.compareTo(membership.getMinSpent()) >= 0)
                .findFirst()
                .map(Membership::getMembershipRank)
                .orElse(MembershipRank.BRONZE);
    }
}
