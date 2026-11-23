package com.thomas.rgsga.config;

import com.thomas.rgsga.entity.Role;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.entity.UserProfile;
import com.thomas.rgsga.repository.UserProfileRepository;
import com.thomas.rgsga.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.provision.email:admin@rbsga.com}")
    private String adminEmail;

    @Value("${admin.provision.password:Admin@RBSGA2026!}")
    private String adminPassword;

    @Value("${admin.provision.name:System Administrator}")
    private String adminName;

    public AdminInitializer(UserRepository userRepository,
                            UserProfileRepository userProfileRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            boolean adminExists = userRepository.existsByRole(Role.ADMIN);
            if (!adminExists) {
                String normalizedEmail = adminEmail.trim().toLowerCase();
                if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                    // Promote existing user to ADMIN if matching email
                    userRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(user -> {
                        user.setRole(Role.ADMIN);
                        user.setAccountEnabled(true);
                        user.setEmailVerified(true);
                        userRepository.save(user);
                        log.info("[SECURITY] Existing account promoted to initial ADMIN: {}", normalizedEmail);
                    });
                } else {
                    User adminUser = User.builder()
                            .name(adminName.trim())
                            .email(normalizedEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .role(Role.ADMIN)
                            .accountEnabled(true)
                            .emailVerified(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    adminUser = userRepository.save(adminUser);

                    UserProfile profile = UserProfile.builder()
                            .user(adminUser)
                            .bio("System Administrator for RBSGA Platform.")
                            .build();
                    userProfileRepository.save(profile);

                    log.info("[SECURITY] Initial Platform Administrator provisioned successfully: email={}", normalizedEmail);
                }
            } else {
                log.info("[SECURITY] Platform Administrator account verified in database.");
            }
        } catch (Exception e) {
            log.error("[SECURITY] Error during admin account initialization: {}", e.getMessage());
        }
    }
}
