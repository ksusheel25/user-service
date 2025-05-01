package com.user_service.service;

import com.user_service.enums.RoleType;
import com.user_service.model.Role;
import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    Role createRole(RoleType roleType);
    void deleteRole(Long id);
}
