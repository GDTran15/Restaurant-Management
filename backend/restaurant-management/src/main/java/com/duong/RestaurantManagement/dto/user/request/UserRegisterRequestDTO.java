package com.duong.RestaurantManagement.dto.user.request;

import com.duong.RestaurantManagement.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserRegisterRequestDTO (
        @NotBlank(message = "Please enter username")
        @Size(min = 5, max = 20 , message = "Username must be bigger than 20 and smaller than 8")
        String username,
        @Email(message = "Email invalid")
        @NotBlank(message = "Please enter your email")
        String email,
        @NotBlank(message = "Please enter your password")
        String password,
        @NotNull(message = "Please enter user phone number")
        Integer phone,
        @NotNull(message = "Please choose a role")
        Role role
) {
}
