package com.purple_dog.mvp.config;

import com.purple_dog.mvp.dao.AdminRepository;
import com.purple_dog.mvp.entities.Admin;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
    }

    private void seedAdminUser() {
        long adminCount = adminRepository.count();
        
        if (adminCount > 0) {
            log.info("Admin user already exists. Skipping seed.");
            return;
        }
        
        try {
            // Create default admin user
            Admin admin = new Admin();
            admin.setEmail("admin@purpledog.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhone("+33 6 00 00 00 00");
            admin.setRole(UserRole.ADMIN);
            admin.setAccountStatus(AccountStatus.ACTIVE);
            admin.setEmailVerified(true);
            admin.setPhoneVerified(true);
            admin.setSuperAdmin(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            adminRepository.save(admin);
            
            log.info("Default admin user created: admin@purpledog.com");
        } catch (Exception e) {
            log.error("Failed to create admin user", e);
        }
    }
}
