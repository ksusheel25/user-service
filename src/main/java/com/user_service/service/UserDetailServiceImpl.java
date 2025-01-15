package com.user_service.service;

import com.user_service.model.User;
import com.user_service.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    UserDetailServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            User users = user.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            SimpleGrantedAuthority sga = new SimpleGrantedAuthority(users.getRole());
            authorities.add(sga);
            return new User(
                    users.getEmail(),
                    users.getPassword()
            ) {
                @Override
                public List<GrantedAuthority> getAuthorities() {
                    return authorities; // Use the authorities we just created
                }
            };
        } else
            throw new UsernameNotFoundException("User Details not found with this username: " + username);
        }
    }

