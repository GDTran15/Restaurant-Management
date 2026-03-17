package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.user.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.dto.user.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.dto.user.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequestDTO userRegisterRequestDTO) {
        userService.registerANewUser(userRegisterRequestDTO);
        return ResponseEntity.ok("User registered successfully");
    }

   @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO userLoginRequestDTO ,
                                                      HttpServletResponse httpServletResponse) {

       return ResponseEntity.ok(userService.loginForUser(userLoginRequestDTO , httpServletResponse));
    }


}
