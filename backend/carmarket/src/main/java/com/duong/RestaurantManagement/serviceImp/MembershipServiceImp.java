package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.member.request.AddMembershipRequest;
import com.duong.RestaurantManagement.model.Membership;
import com.duong.RestaurantManagement.model.MembershipRank;
import com.duong.RestaurantManagement.repo.MembershipRepo;
import com.duong.RestaurantManagement.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
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
    public MembershipRank identifyMembershipRank(double spent) {
        return membershipRepo.findAllByOrderByMinSpentDesc()
                .stream()
                .filter(membership -> spent >= membership.getMinSpent())
                .findFirst()
                .map(Membership::getMembershipRank)
                .orElse(null);
    }
}
