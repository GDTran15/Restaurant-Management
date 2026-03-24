package com.duong.RestaurantManagement.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.duong.RestaurantManagement.model.Permission.*;

@RequiredArgsConstructor
public enum Role {
    ADMIN(
            Set.of(
                ADMIN_READ,
                ADMIN_UPDATE,
                ADMIN_DELETE,
                ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE

            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE

            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
//        var authorities = getPermissions()
//                .stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.name()))
//                .toList();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getPermission()))
        );


        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities; //return a list of role so it will include role and permission
    }
}
