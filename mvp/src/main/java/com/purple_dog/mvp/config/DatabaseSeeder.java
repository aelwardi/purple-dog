package com.purple_dog.mvp.config;

import com.purple_dog.mvp.dao.AdminRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.entities.Admin;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.super-admin.email}")
    private String superAdminEmail;

    @Value("${app.super-admin.password}")
    private String superAdminPassword;

    @Value("${app.super-admin.first-name}")
    private String superAdminFirstName;

    @Value("${app.super-admin.last-name}")
    private String superAdminLastName;

    @Value("${app.super-admin.phone}")
    private String superAdminPhone;

    @Override
    public void run(String... args) {
        seedSuperAdmin();
        seedTestAdmin();
    }

    private void seedSuperAdmin() {
        log.info("🔍 Checking for super admin existence...");
        List<Admin> superAdmins = adminRepository.findBySuperAdmin(true);
        if (!superAdmins.isEmpty()) {
            log.info("✓ Super admin already exists. Found {} super admin(s)", superAdmins.size());
            superAdmins.forEach(admin ->
                log.info("  - Email: {}, Name: {} {}", admin.getEmail(), admin.getFirstName(), admin.getLastName())
            );
            return;
        }
        
        try {
            boolean emailExists = personRepository.existsByEmail(superAdminEmail);
            if (emailExists) {
                log.warn("User with email {} already exists. Checking if it's an admin that can be promoted...", superAdminEmail);

                Optional<Admin> existingAdmin = adminRepository.findByEmail(superAdminEmail);
                if (existingAdmin.isPresent()) {
                    Admin admin = existingAdmin.get();
                    if (!admin.getSuperAdmin()) {
                        admin.setSuperAdmin(true);
                        admin.setPermissions("ALL");
                        admin.setUpdatedAt(LocalDateTime.now());
                        adminRepository.save(admin);
                        log.info("✅ Existing admin promoted to super admin: {}", superAdminEmail);
                    }
                    return;
                }

                log.warn("User exists but is not an admin. Skipping super admin creation.");
                return;
            }

            boolean phoneExists = personRepository.existsByPhone(superAdminPhone);
            if (phoneExists) {
                log.warn("Phone number {} already exists. Using a unique phone number instead.", superAdminPhone);
                superAdminPhone = "+33 6 " + String.format("%02d", LocalDateTime.now().getSecond()) + " " +
                                 String.format("%02d", LocalDateTime.now().getMinute()) + " " +
                                 String.format("%02d", LocalDateTime.now().getHour()) + " " +
                                 String.format("%02d", LocalDateTime.now().getDayOfMonth());
                log.info("  → Using phone: {}", superAdminPhone);
            }

            log.info("Creating super admin...");
            Admin superAdmin = new Admin();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setFirstName(superAdminFirstName);
            superAdmin.setLastName(superAdminLastName);
            superAdmin.setPhone(superAdminPhone);
            superAdmin.setRole(UserRole.ADMIN);
            superAdmin.setAccountStatus(AccountStatus.ACTIVE);
            superAdmin.setEmailVerified(true);
            superAdmin.setPhoneVerified(true);
            superAdmin.setSuperAdmin(true);
            superAdmin.setPermissions("ALL");
            superAdmin.setCreatedAt(LocalDateTime.now());
            superAdmin.setUpdatedAt(LocalDateTime.now());

            adminRepository.save(superAdmin);

            log.info("══════════════════════════════════════════════════════════════");
            log.info("SUPER ADMIN CREATED SUCCESSFULLY!");
            log.info("══════════════════════════════════════════════════════════════");
            log.info("   Email: {}", superAdminEmail);
            log.info("   Password: {}", superAdminPassword);
            log.info("   Name: {} {}", superAdminFirstName, superAdminLastName);
            log.info("   Phone: {}", superAdminPhone);
            log.info("   Role: SUPER_ADMIN");
            log.info("   Permissions: ALL");
            log.info("   Email Verified: YES");
            log.info("   Account Status: ACTIVE");
            log.info("══════════════════════════════════════════════════════════════");
            log.warn("IMPORTANT: Please change the default password after first login!");
            log.info("══════════════════════════════════════════════════════════════");
        } catch (Exception e) {
            log.error("══════════════════════════════════════════════════════════════");
            log.error("FAILED TO CREATE SUPER ADMIN!");
            log.error("══════════════════════════════════════════════════════════════");
            log.error("Error details:", e);
            log.error("══════════════════════════════════════════════════════════════");
        }
    }

    private void seedTestAdmin() {
        log.info("🔍 Checking for test admin existence...");

        final String TEST_ADMIN_EMAIL = "admin@purpledog.com";
        final String TEST_ADMIN_PASSWORD = "Admin@123";
        final String TEST_ADMIN_FIRST_NAME = "Admin";
        final String TEST_ADMIN_LAST_NAME = "Test";
        final String TEST_ADMIN_PHONE = "+33 6 11 22 33 44";

        Optional<Admin> existingTestAdmin = adminRepository.findByEmail(TEST_ADMIN_EMAIL);
        if (existingTestAdmin.isPresent()) {
            log.info("✓ Test admin already exists with email: {}", TEST_ADMIN_EMAIL);
            return;
        }

        boolean emailExists = personRepository.existsByEmail(TEST_ADMIN_EMAIL);
        if (emailExists) {
            log.warn("⚠️ User with email {} already exists but is not an admin. Promoting...", TEST_ADMIN_EMAIL);

            Person person = personRepository.findByEmail(TEST_ADMIN_EMAIL).orElse(null);
            if (person != null) {
                person.setRole(UserRole.ADMIN);
                personRepository.save(person);

                Admin admin = new Admin();
                admin.setId(person.getId());
                admin.setSuperAdmin(false);
                admin.setPermissions("STANDARD");

                try {
                    adminRepository.save(admin);
                    log.info("✅ Existing user promoted to test admin: {}", TEST_ADMIN_EMAIL);
                } catch (Exception e) {
                    log.error("❌ Failed to promote user to admin", e);
                }
            }
            return;
        }

        try {
            String testAdminPhone = TEST_ADMIN_PHONE;
            boolean phoneExists = personRepository.existsByPhone(testAdminPhone);
            if (phoneExists) {
                log.warn("⚠️ Phone number {} already exists. Using a unique phone number instead.", testAdminPhone);
                testAdminPhone = "+33 6 " + String.format("%02d", LocalDateTime.now().getSecond() + 1) + " " +
                                 String.format("%02d", LocalDateTime.now().getMinute()) + " " +
                                 String.format("%02d", LocalDateTime.now().getHour()) + " " +
                                 String.format("%02d", LocalDateTime.now().getDayOfMonth());
                log.info("  → Using phone: {}", testAdminPhone);
            }

            log.info("🚀 Creating test admin...");
            Admin testAdmin = new Admin();
            testAdmin.setEmail(TEST_ADMIN_EMAIL);
            testAdmin.setPassword(passwordEncoder.encode(TEST_ADMIN_PASSWORD));
            testAdmin.setFirstName(TEST_ADMIN_FIRST_NAME);
            testAdmin.setLastName(TEST_ADMIN_LAST_NAME);
            testAdmin.setPhone(testAdminPhone);
            testAdmin.setRole(UserRole.ADMIN);
            testAdmin.setAccountStatus(AccountStatus.ACTIVE);
            testAdmin.setEmailVerified(true);
            testAdmin.setPhoneVerified(true);
            testAdmin.setSuperAdmin(false);
            testAdmin.setPermissions("STANDARD");
            testAdmin.setCreatedAt(LocalDateTime.now());
            testAdmin.setUpdatedAt(LocalDateTime.now());

            adminRepository.save(testAdmin);

            log.info("══════════════════════════════════════════════════════════════");
            log.info("✅ TEST ADMIN CREATED SUCCESSFULLY!");
            log.info("══════════════════════════════════════════════════════════════");
            log.info("   📧 Email: {}", TEST_ADMIN_EMAIL);
            log.info("   🔑 Password: {}", TEST_ADMIN_PASSWORD);
            log.info("   👤 Name: {} {}", TEST_ADMIN_FIRST_NAME, TEST_ADMIN_LAST_NAME);
            log.info("   📱 Phone: {}", testAdminPhone);
            log.info("   🎯 Role: ADMIN (not super admin)");
            log.info("   🔒 Permissions: STANDARD");
            log.info("   ✓ Email Verified: YES");
            log.info("   ✓ Account Status: ACTIVE");
            log.info("══════════════════════════════════════════════════════════════");
            log.info("ℹ️  This is a test admin account for development");
            log.info("══════════════════════════════════════════════════════════════");

        } catch (Exception e) {
            log.error("══════════════════════════════════════════════════════════════");
            log.error("❌ FAILED TO CREATE TEST ADMIN!");
            log.error("══════════════════════════════════════════════════════════════");
            log.error("Error details:", e);
            log.error("══════════════════════════════════════════════════════════════");
        }
    }
}
