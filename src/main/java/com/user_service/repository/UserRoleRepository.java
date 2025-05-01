package com.user_service.repository;

import com.user_service.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}