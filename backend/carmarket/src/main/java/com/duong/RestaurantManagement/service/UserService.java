package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.user.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.dto.user.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.dto.user.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.model.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface UserService {

    void registerANewUser(UserRegisterRequestDTO userRegisterRequestDTO);

    boolean checkIfUsernameExist(User user);

    UserLoginResponseDTO loginForUser(@Valid UserLoginRequestDTO userLoginRequestDTO,
                                      HttpServletResponse httpServletResponse);
}