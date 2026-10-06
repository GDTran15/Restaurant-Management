package com.duong.RestaurantManagement.user.controller;

import com.duong.RestaurantManagement.auth.dto.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.auth.dto.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.auth.dto.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;




}
