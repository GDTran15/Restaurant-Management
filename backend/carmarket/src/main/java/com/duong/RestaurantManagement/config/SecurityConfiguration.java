package com.duong.RestaurantManagement.config;

import com.duong.RestaurantManagement.model.Role;
import com.duong.RestaurantManagement.serviceImp.MyUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.nio.charset.Charset;
import java.util.List;

import static com.duong.RestaurantManagement.model.Permission.*;
import static com.duong.RestaurantManagement.model.Role.ADMIN;
import static com.duong.RestaurantManagement.model.Role.MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final MyUserDetailsService myUserDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> {
                        CorsConfigurationSource source = request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(List.of("http://localhost:5173"));
                            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH"));
                            configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                            configuration.setAllowCredentials(true);
                            return configuration;
                        };
                        c.configurationSource(source);
                    })
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers("/authenticate","/refresh-token","/dining-sessions/**","/menus/**").permitAll()
                                .requestMatchers("/register").hasAnyRole(ADMIN.name())
                                .requestMatchers(HttpMethod.GET, "/users").hasAnyAuthority(ADMIN_READ.getPermission())
                                .requestMatchers(HttpMethod.GET, "/users/{userId}/**").hasAnyAuthority(ADMIN_READ.getPermission(),MANAGER_READ.getPermission())
                                .requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                                .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                                .requestMatchers("/users/**").hasAnyRole(ADMIN.name(),MANAGER.name())
                                .requestMatchers("/roles/**").hasAnyRole(ADMIN.name())
                                .requestMatchers("/menus/**", "/foods/**", "/food-categories/**")
                                .hasAnyAuthority(
                                        ADMIN_READ.getPermission(),
                                        MANAGER_READ.getPermission(),
                                        MANAGER_CREATE.getPermission(),
                                        MANAGER_UPDATE.getPermission(),
                                        MANAGER_DELETE.getPermission()
                                )
                                .requestMatchers("/tables/**")
                                .hasAnyAuthority(
                                        MANAGER_READ.getPermission(),
                                        MANAGER_CREATE.getPermission(),
                                        MANAGER_UPDATE.getPermission(),
                                        MANAGER_DELETE.getPermission()
                                )
                                .requestMatchers("/orders/**")
                                .hasAnyAuthority(
                                        MANAGER_READ.getPermission(),
                                        MANAGER_UPDATE.getPermission(),
                                        MANAGER_CREATE.getPermission()
                                )
//                                        "/roles/**",
//                                        "/orders/**",
//                                        "/dining-sessions/**",
//                                        "/menus/**",
//                                        "/login","/menu-items/**",
//                                        "/food-categories/**",
//                                        "/tables/**",
//                                        "/foods/**",
//                                        "/swagger-ui/**",
//                                        "/v3/api-docs/**",
//                                        "/swagger-ui.html",
//
//                                "/cookies"

                                .anyRequest().authenticated())
                    .sessionManagement(sessionManagement
                            -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler.accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.getWriter().write(
                                            """
                                                    {"message":"You are not allowed to perform this action!"}
                                                    """);

                                  }
                        )
                        )
                .exceptionHandling(exceptionHandler -> exceptionHandler.authenticationEntryPoint(
                        (request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    """
                                           {"message":"Jwt token expired!"}
                                           """
                            );
                        }
                ))
        ;


        return http.build();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(myUserDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }


}