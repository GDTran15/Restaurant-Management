package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {


    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.memberPhone = :memberPhone")
    boolean existsByMemberPhone(@Param("memberPhone") int memberPhone);


    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.memberEmail = :memberEmail")
    boolean existsByMemberEmail(@Param("memberEmail") String memberEmail);

    Optional<Member> findByMemberPhone( int memberPhone);
}
