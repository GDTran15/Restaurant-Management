package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.model.User;
import com.duong.RestaurantManagement.model.UserPrincipal;
import com.duong.RestaurantManagement.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new UserPrincipal(user);
    }
}
