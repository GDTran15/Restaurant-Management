package com.duong.RestaurantManagement.member.mapper;

import com.duong.RestaurantManagement.member.dto.response.GetMemberInfoDTO;
import com.duong.RestaurantManagement.member.model.Member;

public class MemberMapper {

    public static GetMemberInfoDTO memberToGetMemberInfoDTO(Member member){
        return new GetMemberInfoDTO(
                member.getFirstName(),
                member.getLastName(),
                member.getMemberPhone(),
                member.getMemberEmail(),
                member.getMemberRank(),
                member.getTotalSpent()

        );
    }

}
