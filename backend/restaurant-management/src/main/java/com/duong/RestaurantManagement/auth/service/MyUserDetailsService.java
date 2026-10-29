package com.duong.RestaurantManagement.auth.service;

import com.duong.RestaurantManagement.user.model.User;
import com.duong.RestaurantManagement.auth.model.UserPrincipal;
import com.duong.RestaurantManagement.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return new UserPrincipal(user);
    }
}
