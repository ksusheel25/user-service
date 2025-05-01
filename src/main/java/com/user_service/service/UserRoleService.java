package com.user_service.service;

import com.user_service.model.UserRole;
import java.util.List;

public interface UserRoleService {
    void assignRoleToUser(Long userId, Long roleId);
    void removeRoleFromUser(Long userId, Long roleId);
    List<UserRole> getUserRoles(Long userId);
    List<UserRole> getRoleUsers(Long roleId);
    boolean hasRole(Long userId, Long roleId);
}