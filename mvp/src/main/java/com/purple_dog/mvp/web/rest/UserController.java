package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.PersonResponseDTO;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.UserRole;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "User Management", description = "APIs for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<List<PersonResponseDTO>> getAllUsers() {
        log.info("GET /users - Fetching all users");
        List<PersonResponseDTO> users = personRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Fetching user", id);
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return ResponseEntity.ok(mapToResponseDTO(person));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PersonResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("GET /users/email/{} - Fetching user by email", email);
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return ResponseEntity.ok(mapToResponseDTO(person));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<PersonResponseDTO>> getUsersByRole(@PathVariable UserRole role) {
        log.info("GET /users/role/{} - Fetching users by role", role);
        List<PersonResponseDTO> users = personRepository.findByRole(role).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PersonResponseDTO>> getUsersByStatus(@PathVariable AccountStatus status) {
        log.info("GET /users/status/{} - Fetching users by account status", status);
        List<PersonResponseDTO> users = personRepository.findByAccountStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonResponseDTO>> searchUsers(@RequestParam String keyword) {
        log.info("GET /users/search?keyword={} - Searching users", keyword);
        List<PersonResponseDTO> users = personRepository.searchByKeyword(keyword).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("GET /users/stats - Fetching user statistics");

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", personRepository.count());

        Map<String, Long> roleStats = new HashMap<>();
        roleStats.put("individuals", personRepository.countByRole(UserRole.INDIVIDUAL));
        roleStats.put("professionals", personRepository.countByRole(UserRole.PROFESSIONAL));
        roleStats.put("admins", personRepository.countByRole(UserRole.ADMIN));
        stats.put("byRole", roleStats);

        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("active", personRepository.countByAccountStatus(AccountStatus.ACTIVE));
        statusStats.put("pending", personRepository.countByAccountStatus(AccountStatus.PENDING_VERIFICATION));
        statusStats.put("suspended", personRepository.countByAccountStatus(AccountStatus.SUSPENDED));
        statusStats.put("banned", personRepository.countByAccountStatus(AccountStatus.BANNED));
        stats.put("byStatus", statusStats);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        log.info("GET /users/check-email?email={} - Checking if email exists", email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", personRepository.existsByEmail(email));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneExists(@RequestParam String phone) {
        log.info("GET /users/check-phone?phone={} - Checking if phone exists", phone);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", personRepository.existsByPhone(phone));
        return ResponseEntity.ok(response);
    }

    private PersonResponseDTO mapToResponseDTO(Person person) {
        PersonResponseDTO dto = new PersonResponseDTO();
        dto.setId(person.getId());
        dto.setEmail(person.getEmail());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhone(person.getPhone());
        dto.setRole(person.getRole());
        dto.setAccountStatus(person.getAccountStatus());
        dto.setProfilePicture(person.getProfilePicture());
        dto.setBio(person.getBio());
        dto.setEmailVerified(person.getEmailVerified());
        dto.setPhoneVerified(person.getPhoneVerified());
        dto.setCreatedAt(person.getCreatedAt());
        dto.setUpdatedAt(person.getUpdatedAt());
        dto.setLastLoginAt(person.getLastLoginAt());
        return dto;
    }
}
