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

            ),"Admin"
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE

            ),"Manager"
    );

    @Getter
    private final Set<Permission> permissions;

    @Getter
    private final String label;

    public List<SimpleGrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getPermission()))
        );


        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities; //return a list of role so it will include role and permission
    }
}
