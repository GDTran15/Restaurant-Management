package com.duong.RestaurantManagement.auth.service;

import com.duong.RestaurantManagement.auth.model.UserPrincipal;
import com.duong.RestaurantManagement.user.model.Role;
import com.duong.RestaurantManagement.user.model.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void principalAndTokenPurposesAreEnforced() {
        var principal = new UserPrincipal(User.builder().username("admin").password("hash").role(Role.ADMIN).build());
        assertEquals("admin", principal.getUsername());
        assertEquals("hash", principal.getPassword());
        assertTrue(principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        var jwt = new JwtService();
        ReflectionTestUtils.setField(jwt,"secretKey","dGVzdC1vbmx5LXNpZ25pbmcta2V5LTMyaHl0ZXMtbWluaW11bS1sb25n");
        ReflectionTestUtils.setField(jwt,"jwtExpiration",60000L);
        ReflectionTestUtils.setField(jwt,"refreshExpiration",120000L);
        String access=jwt.generateToken(principal), refresh=jwt.generateRefreshToken(principal);
        assertTrue(jwt.validateToken(access,principal));
        assertTrue(jwt.validateRefreshToken(refresh,principal));
        assertFalse(jwt.validateToken(refresh,principal));
        assertFalse(jwt.validateRefreshToken(access,principal));
        assertThrows(JwtException.class, () -> jwt.claims("invalid"));
    }
}
