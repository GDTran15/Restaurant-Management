package com.duong.RestaurantManagement.auth.service;

import com.duong.RestaurantManagement.auth.dto.NewAccessTokenResponse;
import com.duong.RestaurantManagement.auth.dto.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.auth.dto.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.auth.dto.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.common.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.user.model.User;
import com.duong.RestaurantManagement.auth.model.UserPrincipal;
import com.duong.RestaurantManagement.user.repository.UserRepo;
import com.duong.RestaurantManagement.auth.service.AuthenticationService;
import com.duong.RestaurantManagement.auth.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
@org.springframework.transaction.annotation.Transactional
public class AuthenticationServiceImpl  implements AuthenticationService {

    @org.springframework.beans.factory.annotation.Value("${app.cookie-secure}")
    private boolean secureCookie;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    @Transactional
    public void registerANewUser(UserRegisterRequestDTO userRegisterRequestDTO) {
        boolean usernameExist = userRepo.existsByUsername(userRegisterRequestDTO.username());
        boolean emailExist = userRepo.existsByEmail(userRegisterRequestDTO.email());
        boolean phoneExist = userRepo.existsByPhone(userRegisterRequestDTO.phone());
        Map<String,String> map = new HashMap<>();
        if (usernameExist) {
            map.put("username","Username already exists");
        }
        if (emailExist) {
            map.put("email","Email already exists");
        }
        if (phoneExist) {
            map.put("phone","Phone already exists");
        }
        if (!map.isEmpty()){
            throw new DuplicateResourceException(map);
        }
        User user = User.builder()
                .username(userRegisterRequestDTO.username())
                .password(bCryptPasswordEncoder.encode(userRegisterRequestDTO.password()))
                .email(userRegisterRequestDTO.email())
                .phone(userRegisterRequestDTO.phone())
                .createdAt(LocalDate.now())
                .role(userRegisterRequestDTO.role())
                .build();
        userRepo.save(user);
    }


    public UserLoginResponseDTO loginForUser(@Valid UserLoginRequestDTO userLoginRequestDTO, HttpServletResponse httpServletResponse) {
        Authentication authentication= authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(userLoginRequestDTO.username(), userLoginRequestDTO.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setMaxAge(Math.toIntExact(jwtService.getRefreshExpirationSeconds()));
        cookie.setAttribute("SameSite", "Strict");
        cookie.setHttpOnly(true); //mean no js
        cookie.setPath("/");    // send cookies to all
        cookie.setSecure(secureCookie); //true when deploy
        httpServletResponse.addCookie(cookie); // 1 day
        httpServletResponse.setStatus(200);
        User user = userRepo.findByUsername(userLoginRequestDTO.username()).get();
        return new UserLoginResponseDTO(user.getUsername(), user.getRole(), accessToken);
    }

    @Override
    public void refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!"refreshToken".equals(cookie.getName())) continue;
                try {
                    String token = cookie.getValue();
                    UserDetails user = myUserDetailsService.loadUserByUsername(jwtService.extractUsername(token));
                    if (jwtService.validateRefreshToken(token, user)) {
                        httpServletResponse.setContentType("application/json");
                        new ObjectMapper().writeValue(httpServletResponse.getOutputStream(),
                                new NewAccessTokenResponse(jwtService.generateToken(user)));
                        return;
                    }
                } catch (io.jsonwebtoken.JwtException | IllegalArgumentException | org.springframework.security.core.AuthenticationException ex) {
                    // Invalid refresh credentials are always unauthorized.
                }
            }
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


}
