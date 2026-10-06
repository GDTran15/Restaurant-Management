package com.duong.RestaurantManagement.member.repository;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.member.model.Membership;
import com.duong.RestaurantManagement.member.model.MembershipRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MembershipRepo extends JpaRepository<Membership, Long> {

    @Query("""
            select ms.discountRate from  Membership ms
                        where ms.membershipRank = :membershipRank
            """)
    BigDecimal findMembershipDiscountRateByMembershipRank(@Param("membershipRank") MembershipRank membershipRank);

    List<Membership> findAllByOrderByMinSpentDesc();
}
