package com.duong.RestaurantManagement.user.service;

import com.duong.RestaurantManagement.auth.dto.request.UserLoginRequestDTO;
import com.duong.RestaurantManagement.auth.dto.request.UserRegisterRequestDTO;
import com.duong.RestaurantManagement.auth.dto.response.UserLoginResponseDTO;
import com.duong.RestaurantManagement.user.model.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface UserService {

    void registerANewUser(UserRegisterRequestDTO userRegisterRequestDTO);

    boolean checkIfUsernameExist(User user);

//    UserLoginResponseDTO loginForUser(@Valid UserLoginRequestDTO userLoginRequestDTO,
//                                      HttpServletResponse httpServletResponse);
}