package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.user.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.dto.user.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.dto.user.response.UserLoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;

public interface AuthenticationService {

    void registerANewUser(UserRegisterRequestDTO userRegisterRequestDTO);

        UserLoginResponseDTO loginForUser(@Valid UserLoginRequestDTO userLoginRequestDTO,
                                          HttpServletResponse httpServletResponse);

    void refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
}
