package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dao.AdminRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.entities.Admin;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/create-admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCreationController {

    private final AdminRepository adminRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint temporaire pour créer un admin - À SUPPRIMER EN PRODUCTION!
     */
    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestParam String email, 
                                         @RequestParam String password) {
        try {
            // Check if admin already exists
            if (personRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Un utilisateur avec cet email existe déjà"));
            }

            // Create admin
            Admin admin = new Admin();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setPhone("+33 6 00 00 00 00");
            admin.setRole(UserRole.ADMIN);
            admin.setAccountStatus(AccountStatus.ACTIVE);
            admin.setEmailVerified(true);
            admin.setPhoneVerified(true);
            admin.setSuperAdmin(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            Admin savedAdmin = adminRepository.save(admin);
            
            log.info("✅ Super Admin created: {} (ID: {})", savedAdmin.getEmail(), savedAdmin.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Super admin créé avec succès!");
            response.put("email", savedAdmin.getEmail());
            response.put("role", savedAdmin.getRole());
            response.put("id", savedAdmin.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error creating admin", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de la création: " + e.getMessage()));
        }
    }
}
