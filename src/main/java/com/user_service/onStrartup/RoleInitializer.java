package com.user_service.onStrartup;

import com.user_service.enums.RoleType;
import com.user_service.model.Role;
import com.user_service.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName(RoleType.USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleType.USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(RoleType.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleType.ADMIN);
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName(RoleType.SUPER_ADMIN).isEmpty()) {
            Role modRole = new Role();
            modRole.setName(RoleType.SUPER_ADMIN);
            roleRepository.save(modRole);
        }
    }
}

