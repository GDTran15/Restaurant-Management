package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.role.response.GetRoleDTO;
import com.duong.RestaurantManagement.model.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @GetMapping
    public List<GetRoleDTO> getRoles() {
        return Arrays.stream(Role.values())
                .map(role ->
                        new GetRoleDTO(
                                role,
                                role.getLabel()
                )).toList();
    }
}
