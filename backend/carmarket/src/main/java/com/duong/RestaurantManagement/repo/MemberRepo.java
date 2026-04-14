package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepo extends JpaRepository<Member, Long> {

    boolean existsByMemberPhone(int memberPhone);

    boolean existsByMemberEmail(String memberEmail);

    Optional<Member> findByMemberPhone( int memberPhone);
}
