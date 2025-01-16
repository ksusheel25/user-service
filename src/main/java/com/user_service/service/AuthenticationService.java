package com.user_service.service;

import com.user_service.dtos.LoginUserDto;
import com.user_service.dtos.RegisterUserDto;
import com.user_service.enums.RoleType;
import com.user_service.model.Role;
import com.user_service.model.User;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User signup(RegisterUserDto input) {
        Set<Role> roleEntities = new HashSet<>();
        for (String roleName : input.getRoles()) {
            RoleType roleType = RoleType.valueOf(roleName.toUpperCase()); // Convert string to enum
            Optional<Role> role = roleRepository.findByName(roleType);
            if (role.isPresent()) {
                roleEntities.add(role.get()); // Add role to the user's roles
            } else {
                throw new IllegalArgumentException("Role not found: " + roleName);
            }
        }
        User user = User.builder()
                .fullName(input.getFullName())
                .email(input.getEmail())
                .roles(roleEntities)
                .password(passwordEncoder.encode(input.getPassword())).build();
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
