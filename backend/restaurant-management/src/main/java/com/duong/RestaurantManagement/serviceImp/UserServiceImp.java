package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.user.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.dto.user.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.dto.user.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.model.User;
import com.duong.RestaurantManagement.model.UserPrincipal;
import com.duong.RestaurantManagement.repo.UserRepo;
import com.duong.RestaurantManagement.service.JwtService;
import com.duong.RestaurantManagement.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final JwtService jwtService;

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

    public boolean checkIfUsernameExist(User user){
        return userRepo.existsByUsername(user.getUsername());

    }

//    public UserLoginResponseDTO loginForUser(@Valid UserLoginRequestDTO userLoginRequestDTO, HttpServletResponse httpServletResponse) {
//        Authentication authentication= authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(userLoginRequestDTO.username(), userLoginRequestDTO.password()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
//        String token = jwtService.generateToken(userPrincipal);
//        Cookie cookie = new Cookie("token", token);
//        cookie.setMaxAge(60*60*24);
//        cookie.setHttpOnly(true); //mean no js
//        cookie.setPath("/");    // send cookies to all
//        cookie.setSecure(false); //
//        httpServletResponse.addCookie(cookie); // 1 day
//        httpServletResponse.setStatus(200);
//        User user = userRepo.findByUsername(userLoginRequestDTO.username()).get();
//        return new UserLoginResponseDTO(user.getUsername(), user.getRole());
//    }
}

