package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Membership;
import com.duong.RestaurantManagement.model.MembershipRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembershipRepo extends JpaRepository<Membership, Long> {

    @Query("""
            select ms.discountRate from  Membership ms
                        where ms.membershipRank = :membershipRank
            """)
    double findMembershipDiscountRateByMembershipRank(@Param("membershipRank") MembershipRank membershipRank);
}
