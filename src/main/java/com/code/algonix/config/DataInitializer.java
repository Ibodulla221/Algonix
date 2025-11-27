package com.code.algonix.config;

import com.code.algonix.user.Role;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .email("admin@algonix.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("✔ Default admin user created - username: admin, password: admin123");
        }

        // Create test user if not exists
        if (userRepository.findByUsername("testuser").isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .username("testuser")
                    .email("testuser@algonix.com")
                    .password(passwordEncoder.encode("test123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            log.info("✔ Test user created - username: testuser, password: test123");
        }
    }
}
