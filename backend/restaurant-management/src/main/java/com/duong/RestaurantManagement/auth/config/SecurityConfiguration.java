package com.duong.RestaurantManagement.auth.config;
import com.duong.RestaurantManagement.auth.service.MyUserDetailsService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final MyUserDetailsService userDetailsService;

    @Value("${app.frontend-url}") private String frontendUrl;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(frontendUrl));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        // Browser upgrades cannot carry a bearer header; STOMP CONNECT authenticates it.
                        .requestMatchers(HttpMethod.GET, "/orders/websocket").permitAll()
                        .requestMatchers(HttpMethod.POST, "/authenticate", "/refresh-token", "/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/dining-sessions", "/menus/active").permitAll()
                        .requestMatchers("/register", "/users/**", "/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/orders", "/payments/paypal/orders", "/payments/paypal/orders/*/capture",
                                "/api/payments/paypal/orders", "/api/payments/paypal/orders/*/capture").authenticated()
                        .requestMatchers(HttpMethod.GET, "/orders", "/dining-sessions/*/order-items", "/invoices/active", "/invoices/*").authenticated()
                        .anyRequest().hasAnyRole("ADMIN", "MANAGER"))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint((q, r, ex) -> r.sendError(401))
                        .accessDeniedHandler((q, r, ex) -> r.sendError(403)))
                .build();
    }
    @Bean
    AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return provider;
    }
    @Bean
    AuthenticationManager authenticationManager() { return new ProviderManager(authenticationProvider()); }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
