package com.duong.RestaurantManagement.member.service;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.dto.request.AddMemberRequest;
import com.duong.RestaurantManagement.member.dto.response.GetMemberInfoDTO;
import com.duong.RestaurantManagement.common.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.member.mapper.MemberMapper;
import com.duong.RestaurantManagement.member.model.Member;
import com.duong.RestaurantManagement.member.repository.MemberRepo;
import com.duong.RestaurantManagement.member.service.MemberService;
import com.duong.RestaurantManagement.member.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
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
    public void updateMemberAfterPayment(Member member, BigDecimal totalPay) {
        member = memberRepo.findLockedById(member.getMember_id()).orElseThrow();
        member.setTotalSpent(member.getTotalSpent().add(totalPay));
        updateMemberRankAfter(member);
        memberRepo.save(member);

    }

    @Override
    public GetMemberInfoDTO getMemberByPhoneNumber(String phoneNumber) {
       return   MemberMapper.memberToGetMemberInfoDTO(
                memberRepo.findByMemberPhone(phoneNumber).orElseThrow(
                        () -> new ResourceNotFoundException("Cannot find member")
                ));

    }

    private void checkIfMemberInformationExist(String memberPhone, String memberEmail){
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
