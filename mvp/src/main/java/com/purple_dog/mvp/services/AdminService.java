package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AdminRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.AdminCreateDTO;
import com.purple_dog.mvp.dto.AdminResponseDTO;
import com.purple_dog.mvp.dto.UserUpdateDTO;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.Admin;
import com.purple_dog.mvp.entities.UserRole;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final PersonRepository personRepository;

    public AdminResponseDTO createAdmin(AdminCreateDTO dto) {
        log.info("Creating admin with email: {}", dto.getEmail());

        if (personRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        if (dto.getPhone() != null && personRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
        }

        Admin admin = Admin.builder()
                .email(dto.getEmail())
                .password(dto.getPassword()) // TODO: Encoder le mot de passe avec BCrypt
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .role(UserRole.ADMIN)
                .accountStatus(AccountStatus.ACTIVE)
                .profilePicture(dto.getProfilePicture())
                .bio(dto.getBio())
                .emailVerified(true)
                .phoneVerified(false)
                .superAdmin(dto.getSuperAdmin() != null ? dto.getSuperAdmin() : false)
                .permissions(dto.getPermissions())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Admin saved = adminRepository.save(admin);
        log.info("Admin created successfully with ID: {}", saved.getId());

        return mapToResponseDTO(saved);
    }

    public AdminResponseDTO getAdminById(Long id) {
        log.info("Fetching admin with ID: {}", id);
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));
        return mapToResponseDTO(admin);
    }

    public List<AdminResponseDTO> getAllAdmins() {
        log.info("Fetching all admins");
        return adminRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AdminResponseDTO> getSuperAdmins() {
        log.info("Fetching super admins");
        return adminRepository.findBySuperAdmin(true).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public AdminResponseDTO updateAdmin(Long id, UserUpdateDTO dto) {
        log.info("Updating admin with ID: {}", id);
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        if (dto.getFirstName() != null) admin.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) admin.setLastName(dto.getLastName());
        if (dto.getPhone() != null) {
            if (!dto.getPhone().equals(admin.getPhone()) && personRepository.existsByPhone(dto.getPhone())) {
                throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
            }
            admin.setPhone(dto.getPhone());
        }
        if (dto.getProfilePicture() != null) admin.setProfilePicture(dto.getProfilePicture());
        if (dto.getBio() != null) admin.setBio(dto.getBio());

        admin.setUpdatedAt(LocalDateTime.now());
        Admin updated = adminRepository.save(admin);
        log.info("Admin updated successfully with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public AdminResponseDTO updatePermissions(Long id, String permissions) {
        log.info("Updating permissions for admin with ID: {}", id);
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        admin.setPermissions(permissions);
        admin.setUpdatedAt(LocalDateTime.now());

        Admin updated = adminRepository.save(admin);
        log.info("Permissions updated for admin with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public AdminResponseDTO promotToSuperAdmin(Long id) {
        log.info("Promoting admin with ID: {} to super admin", id);
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        admin.setSuperAdmin(true);
        admin.setUpdatedAt(LocalDateTime.now());

        Admin updated = adminRepository.save(admin);
        log.info("Admin promoted to super admin with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public void deleteAdmin(Long id) {
        log.info("Deleting admin with ID: {}", id);
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin not found with ID: " + id);
        }
        adminRepository.deleteById(id);
        log.info("Admin deleted successfully with ID: {}", id);
    }

    public long countAdmins() {
        return adminRepository.count();
    }

    public long countSuperAdmins() {
        return adminRepository.countBySuperAdmin(true);
    }

    private AdminResponseDTO mapToResponseDTO(Admin admin) {
        AdminResponseDTO dto = new AdminResponseDTO();
        dto.setId(admin.getId());
        dto.setEmail(admin.getEmail());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setPhone(admin.getPhone());
        dto.setRole(admin.getRole());
        dto.setAccountStatus(admin.getAccountStatus());
        dto.setProfilePicture(admin.getProfilePicture());
        dto.setBio(admin.getBio());
        dto.setEmailVerified(admin.getEmailVerified());
        dto.setPhoneVerified(admin.getPhoneVerified());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setUpdatedAt(admin.getUpdatedAt());
        dto.setLastLoginAt(admin.getLastLoginAt());
        dto.setSuperAdmin(admin.getSuperAdmin());
        dto.setPermissions(admin.getPermissions());
        return dto;
    }
}

