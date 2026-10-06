package com.duong.RestaurantManagement.auth.config;
import com.duong.RestaurantManagement.auth.service.JwtService;
import com.duong.RestaurantManagement.auth.service.MyUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final MyUserDetailsService myUserDetailsService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        try {
            if (header != null && header.startsWith("Bearer ")) {
                var claims = jwtService.claims(header.substring(7));
                String purpose = claims.get("purpose", String.class);
                UsernamePasswordAuthenticationToken authentication;
                if ("dining".equals(purpose)) {
                    authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
                } else if ("access".equals(purpose)) {
                    var user = myUserDetailsService.loadUserByUsername(claims.getSubject());
                    authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                } else throw new BadCredentialsException("Invalid token purpose");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(401, "Invalid or expired access token");
            return;
        }
        chain.doFilter(request, response);
    }
}
