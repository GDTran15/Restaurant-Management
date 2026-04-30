package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.member.request.AddMemberRequest;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.model.Member;
import com.duong.RestaurantManagement.repo.MemberRepo;
import com.duong.RestaurantManagement.service.MemberService;
import com.duong.RestaurantManagement.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImp implements MemberService {

    private final MemberRepo memberRepo;
    private final MembershipService membershipService;



    @Override
    public void addMember(AddMemberRequest addMemberRequest) {
        log.info("add member service");
        log.debug("add member request: {}", addMemberRequest);
        checkIfMemberInformationExist(addMemberRequest.memberPhone(), addMemberRequest.memberEmail());
        Member member =  Member.builder()
                .firstName(addMemberRequest.firstName())
                .lastName(addMemberRequest.lastName())
                .memberPhone(addMemberRequest.memberPhone())
                .memberEmail(addMemberRequest.memberEmail())
                .memberRank(membershipService.identifyMembershipRank(addMemberRequest.totalSpent()))
                .totalSpent(addMemberRequest.totalSpent())
                .build();
        memberRepo.save(member);
    }

    @Override
    public void updateMemberAfterPayment(Member member, double totalPay) {
        member.setTotalSpent(member.getTotalSpent() + totalPay);
        updateMemberRankAfter(member);
        memberRepo.save(member);

    }

    private void checkIfMemberInformationExist(int memberPhone, String memberEmail){
       boolean memberPhoneExist = memberRepo.existsByMemberPhone(memberPhone);
       boolean memberEmailExist = memberRepo.existsByMemberEmail(memberEmail);
       Map<String,String> errors = new HashMap<>();
       if (memberPhoneExist){
           errors.put("memberPhone", "This phone number is already in used");
       }
       if (memberEmailExist){
           errors.put("memberEmail", "This member email is already in used");
       }
       if(!errors.isEmpty()){
           throw new DuplicateResourceException(errors);
       }
    }

    private void updateMemberRankAfter(Member member){
        member.setMemberRank(membershipService.identifyMembershipRank(member.getTotalSpent()));
    }

}
