package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.auth.NewAccessTokenResponse;
import com.duong.RestaurantManagement.dto.user.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.dto.user.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.dto.user.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.model.User;
import com.duong.RestaurantManagement.model.UserPrincipal;
import com.duong.RestaurantManagement.repo.UserRepo;
import com.duong.RestaurantManagement.service.AuthenticationService;
import com.duong.RestaurantManagement.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
public class AuthenticationServiceImpl  implements AuthenticationService {

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

        cookie.setMaxAge(60*60*24);
        cookie.setHttpOnly(true); //mean no js
        cookie.setPath("/");    // send cookies to all
        cookie.setSecure(false); //true when deploy
        httpServletResponse.addCookie(cookie); // 1 day
        httpServletResponse.setStatus(200);
        User user = userRepo.findByUsername(userLoginRequestDTO.username()).get();
        return new UserLoginResponseDTO(user.getUsername(), user.getRole(), accessToken);
    }

    @Override
    public void refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        Cookie[] cookie = httpServletRequest.getCookies();
        String refreshToken = "";
        for (Cookie c : cookie) {
            if (c.getName().equals("refreshToken")) {
                refreshToken = c.getValue();
                break;
            }
        }

        String username = null;
        if (refreshToken.isEmpty()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(refreshToken, userDetails)) {
               var accessToken = jwtService.generateToken(userDetails);

               var authResponse = new NewAccessTokenResponse(accessToken);
               new ObjectMapper().writeValue(httpServletResponse.getOutputStream(),authResponse);
            }
        }
    }


}
