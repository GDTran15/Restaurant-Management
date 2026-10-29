package com.duong.RestaurantManagement.auth.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
@Service
public class JwtService {
    @Value("${jwt.secret}") private String secretKey;
    @Value("${jwt.expiration}") private long jwtExpiration;
    @Value("${refresh.expiration}") private long refreshExpiration;

    public String generateToken(UserDetails user) {
        return build(user.getUsername(), "access", jwtExpiration); }

    public String generateRefreshToken(UserDetails user) {
        return build(user.getUsername(), "refresh", refreshExpiration); }

    public String generateDiningToken(Long sessionId) {
        return build(sessionId.toString(), "dining", 12 * 60 * 60 * 1000L); }

    public long getRefreshExpirationSeconds() {
        return refreshExpiration / 1000; }

    private String build(String subject, String purpose, long lifetime) {
        return Jwts.builder()
                   .id(UUID.randomUUID().toString())
                   .subject(subject)
                   .claim("purpose", purpose)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + lifetime))
                   .signWith(key())
                   .compact();
    }

    private SecretKey key() {
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }
    public Claims claims(String token) {
        return Jwts.parser().
                   verifyWith(key())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
    public String extractUsername(String token) {
        return claims(token).getSubject();
    }
    public boolean validateToken(String token, UserDetails user) {
        return valid(token, user, "access");
    }
    public boolean validateRefreshToken(String token, UserDetails user) {
        return valid(token, user, "refresh");
    }
    private boolean valid(String token, UserDetails user, String purpose) {
        Claims claims = claims(token);
        return user.getUsername().equals(claims.getSubject()) && purpose.equals(claims.get("purpose", String.class));
    }
}
