package com.user_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.user_service.enums.RoleType;
import com.user_service.models.Role;
import com.user_service.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner{
	private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            log.info("Initializing default roles...");

            Role admin = Role.builder()
                    .name(RoleType.ADMIN)
                    .description("System administrator with full access")
                    .build();

            Role manager = Role.builder()
                    .name(RoleType.MANAGER)
                    .description("Project manager with team oversight")
                    .build();

            Role developer = Role.builder()
                    .name(RoleType.DEVELOPER)
                    .description("Developer with bug management access")
                    .build();

            Role viewer = Role.builder()
                    .name(RoleType.VIEWER)
                    .description("Read-only access to projects")
                    .build();

            roleRepository.save(admin);
            roleRepository.save(manager);
            roleRepository.save(developer);
            roleRepository.save(viewer);

            log.info("Default roles created successfully");
        }
    }
}
