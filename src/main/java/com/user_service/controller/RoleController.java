package com.user_service.controller;

import com.user_service.enums.RoleType;
import com.user_service.model.Role;
import com.user_service.model.UserRole;
import com.user_service.service.RoleService;
import com.user_service.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN', 'SUPER_ADMIN')")
public class RoleController {
    
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleType roleType) {
        Role createdRole = roleService.createRole(roleType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roleId}/assign/{userId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long roleId, @PathVariable Long userId) {
        userRoleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/remove/{userId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long roleId, @PathVariable Long userId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roleId}/users")
    public ResponseEntity<List<UserRole>> getUsersByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(userRoleService.getRoleUsers(roleId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserRole>> getRolesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.getUserRoles(userId));
    }
}
