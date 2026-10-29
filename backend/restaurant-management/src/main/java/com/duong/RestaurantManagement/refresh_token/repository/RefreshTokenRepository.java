package com.duong.RestaurantManagement.refresh_token.repository;

import com.duong.RestaurantManagement.refresh_token.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByTokenAndUser_UsernameAndIsRevokedFalseAndExpireAtAfter(
            String token, String username, java.time.Instant now);

    @Modifying
    @Transactional
    @Query("""
    update RefreshToken r
    set r.isRevoked = true
    where r.token = :token
""")
    int revokeByToken(@Param("token") String token);
}
