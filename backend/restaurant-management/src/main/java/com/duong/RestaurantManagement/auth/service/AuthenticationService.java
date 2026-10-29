package com.duong.RestaurantManagement.auth.service;

import com.duong.RestaurantManagement.auth.dto.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.auth.dto.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.auth.dto.response.UserLoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;

public interface AuthenticationService {

    void registerANewUser(UserRegisterRequestDTO userRegisterRequestDTO);

    UserLoginResponseDTO loginForUser(@Valid UserLoginRequestDTO userLoginRequestDTO,
                                          HttpServletResponse httpServletResponse);

    void refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;

    void logoutForUser(String refreshToken, HttpServletResponse response);
}
