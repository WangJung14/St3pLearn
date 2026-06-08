package com.tommy.identity.infrastructure.seeder;

import com.tommy.identity.domain.entity.Permission;
import com.tommy.identity.domain.entity.Role;
import com.tommy.identity.infrastructure.persistence.repository.PermissionRepository;
import com.tommy.identity.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j // Dùng để in log ra console
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking and initializing database...");
        seedPermissions();
        seedRoles();
        log.info("Database initialization completed!");
    }

    private void seedPermissions() {
        List<String> permissionCodes = List.of(
                "course.create", "course.update", "course.delete",
                "user.manage", "payment.refund"
        );

        for (String code : permissionCodes) {
            if (permissionRepository.findByCode(code).isEmpty()) {
                Permission permission = Permission.builder()
                        .code(code)
                        .description("Permission: " + code)
                        .build();
                permissionRepository.save(permission);
                log.info("Inserted permission: {}", code);
            }
        }
    }

    private void seedRoles() {
        List<String> roleNames = List.of("ADMIN", "TEACHER", "STUDENT", "MODERATOR");

        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder()
                        .name(roleName)
                        .description("Role: " + roleName)
                        .build();
                roleRepository.save(role);
                log.info("Inserted role: {}", roleName);
            }
        }
    }
}
