package com.user_service.service.impl;

import com.user_service.model.Role;
import com.user_service.model.User;
import com.user_service.model.UserRole;
import com.user_service.repository.RoleRepository;
import com.user_service.repository.UserRepository;
import com.user_service.repository.UserRoleRepository;
import com.user_service.service.UserRoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        if (hasRole(userId, roleId)) {
            return; // Role already assigned
        }

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        userRoleRepository.save(userRole);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        if (!roleRepository.existsById(roleId)) {
            throw new EntityNotFoundException("Role not found with id: " + roleId);
        }
        
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public List<UserRole> getUserRoles(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        return userRoleRepository.findByUserId(userId);
    }

    @Override
    public List<UserRole> getRoleUsers(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new EntityNotFoundException("Role not found with id: " + roleId);
        }
        return userRoleRepository.findByRoleId(roleId);
    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        return userRoleRepository.findByUserIdAndRoleId(userId, roleId).isPresent();
    }
}