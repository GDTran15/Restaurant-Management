package com.duong.RestaurantManagement.member.repository;

import com.duong.RestaurantManagement.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Member e where e.member_id = :id")
    java.util.Optional<Member> findLockedById(@org.springframework.data.repository.query.Param("id") Long id);



    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.memberPhone = :memberPhone")
    boolean existsByMemberPhone(@Param("memberPhone") String memberPhone);


    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.memberEmail = :memberEmail")
    boolean existsByMemberEmail(@Param("memberEmail") String memberEmail);

    Optional<Member> findByMemberPhone( String memberPhone);
}
