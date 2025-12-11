package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PasswordResetTokenRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.ProfessionalRepository;
import com.purple_dog.mvp.dto.ProfessionalCreateDTO;
import com.purple_dog.mvp.dto.ProfessionalResponseDTO;
import com.purple_dog.mvp.dto.ProfessionalUpdateDTO;
import com.purple_dog.mvp.dto.RegisterProfessionalDTO;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.Professional;
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
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final PersonRepository personRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public ProfessionalResponseDTO createProfessional(RegisterProfessionalDTO dto) {
        log.info("Creating professional from registration with email: {}", dto.getEmail());

        if (personRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        if (dto.getPhone() != null && personRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
        }

        if (professionalRepository.existsBySiret(dto.getSiret())) {
            throw new DuplicateResourceException("SIRET already exists: " + dto.getSiret());
        }

        Professional professional = Professional.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .role(UserRole.PROFESSIONAL)
                .accountStatus(AccountStatus.PENDING_VERIFICATION)
                .profilePicture(dto.getProfilePicture())
                .bio(dto.getBio())
                .emailVerified(false)
                .phoneVerified(false)
                .companyName(dto.getCompanyName())
                .siret(dto.getSiret())
                .tvaNumber(dto.getVatNumber())
                .certified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Professional saved = professionalRepository.save(professional);
        log.info("Professional created successfully with ID: {}", saved.getId());

        return mapToResponseDTO(saved);
    }

    public ProfessionalResponseDTO createProfessional(ProfessionalCreateDTO dto) {
        log.info("Creating professional with email: {}", dto.getEmail());

        if (personRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        if (dto.getPhone() != null && personRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
        }

        if (professionalRepository.existsBySiret(dto.getSiret())) {
            throw new DuplicateResourceException("SIRET already exists: " + dto.getSiret());
        }

        if (dto.getTvaNumber() != null && professionalRepository.existsByTvaNumber(dto.getTvaNumber())) {
            throw new DuplicateResourceException("TVA number already exists: " + dto.getTvaNumber());
        }

        Professional professional = Professional.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .role(UserRole.PROFESSIONAL)
                .accountStatus(AccountStatus.PENDING_VERIFICATION)
                .profilePicture(dto.getProfilePicture())
                .bio(dto.getBio())
                .emailVerified(false)
                .phoneVerified(false)
                .companyName(dto.getCompanyName())
                .siret(dto.getSiret())
                .tvaNumber(dto.getTvaNumber())
                .website(dto.getWebsite())
                .companyDescription(dto.getCompanyDescription())
                .specialty(dto.getSpecialty())
                .certified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Professional saved = professionalRepository.save(professional);
        log.info("Professional created successfully with ID: {}", saved.getId());

        return mapToResponseDTO(saved);
    }

    public ProfessionalResponseDTO getProfessionalById(Long id) {
        log.info("Fetching professional with ID: {}", id);
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professional not found with ID: " + id));
        return mapToResponseDTO(professional);
    }

    public List<ProfessionalResponseDTO> getAllProfessionals() {
        log.info("Fetching all professionals");
        return professionalRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProfessionalResponseDTO> getCertifiedProfessionals(Boolean certified) {
        log.info("Fetching professionals by certification status: {}", certified);
        return professionalRepository.findByCertified(certified).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProfessionalResponseDTO> getProfessionalsBySpecialty(String specialty) {
        log.info("Fetching professionals by specialty: {}", specialty);
        return professionalRepository.findBySpecialty(specialty).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProfessionalResponseDTO> searchByCompanyName(String keyword) {
        log.info("Searching professionals by company name: {}", keyword);
        return professionalRepository.searchByCompanyName(keyword).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ProfessionalResponseDTO updateProfessional(Long id, ProfessionalUpdateDTO dto) {
        log.info("Updating professional with ID: {}", id);
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professional not found with ID: " + id));

        if (dto.getFirstName() != null) professional.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) professional.setLastName(dto.getLastName());
        if (dto.getPhone() != null) {
            if (!dto.getPhone().equals(professional.getPhone()) && personRepository.existsByPhone(dto.getPhone())) {
                throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
            }
            professional.setPhone(dto.getPhone());
        }
        if (dto.getProfilePicture() != null) professional.setProfilePicture(dto.getProfilePicture());
        if (dto.getBio() != null) professional.setBio(dto.getBio());
        if (dto.getCompanyName() != null) professional.setCompanyName(dto.getCompanyName());
        if (dto.getWebsite() != null) professional.setWebsite(dto.getWebsite());
        if (dto.getCompanyDescription() != null) professional.setCompanyDescription(dto.getCompanyDescription());
        if (dto.getSpecialty() != null) professional.setSpecialty(dto.getSpecialty());
        if (dto.getCertificationUrl() != null) professional.setCertificationUrl(dto.getCertificationUrl());

        professional.setUpdatedAt(LocalDateTime.now());
        Professional updated = professionalRepository.save(professional);
        log.info("Professional updated successfully with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public void deleteProfessional(Long id) {
        log.info("Deleting professional with ID: {}", id);
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professional not found with ID: " + id));

        // Nettoyer les tokens de réinitialisation de mot de passe avant suppression
        log.debug("Cleaning password reset tokens for professional ID: {}", id);
        passwordResetTokenRepository.deleteByPerson(professional);

        // Supprimer le professionnel
        professionalRepository.deleteById(id);
        log.info("Professional deleted successfully with ID: {}", id);
    }

    public ProfessionalResponseDTO certifyProfessional(Long id) {
        log.info("Certifying professional with ID: {}", id);
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professional not found with ID: " + id));

        professional.setCertified(true);
        professional.setAccountStatus(AccountStatus.ACTIVE);
        professional.setUpdatedAt(LocalDateTime.now());

        Professional updated = professionalRepository.save(professional);
        log.info("Professional certified with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public ProfessionalResponseDTO updateAccountStatus(Long id, AccountStatus status) {
        log.info("Updating account status for professional with ID: {} to {}", id, status);
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professional not found with ID: " + id));

        professional.setAccountStatus(status);
        professional.setUpdatedAt(LocalDateTime.now());

        Professional updated = professionalRepository.save(professional);
        log.info("Account status updated for professional with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public long countProfessionals() {
        return professionalRepository.count();
    }

    public long countCertifiedProfessionals() {
        return professionalRepository.countByCertified(true);
    }

    private ProfessionalResponseDTO mapToResponseDTO(Professional professional) {
        ProfessionalResponseDTO dto = new ProfessionalResponseDTO();
        dto.setId(professional.getId());
        dto.setEmail(professional.getEmail());
        dto.setFirstName(professional.getFirstName());
        dto.setLastName(professional.getLastName());
        dto.setPhone(professional.getPhone());
        dto.setRole(professional.getRole());
        dto.setAccountStatus(professional.getAccountStatus());
        dto.setProfilePicture(professional.getProfilePicture());
        dto.setBio(professional.getBio());
        dto.setEmailVerified(professional.getEmailVerified());
        dto.setPhoneVerified(professional.getPhoneVerified());
        dto.setCreatedAt(professional.getCreatedAt());
        dto.setUpdatedAt(professional.getUpdatedAt());
        dto.setLastLoginAt(professional.getLastLoginAt());
        dto.setCompanyName(professional.getCompanyName());
        dto.setSiret(professional.getSiret());
        dto.setTvaNumber(professional.getTvaNumber());
        dto.setWebsite(professional.getWebsite());
        dto.setCompanyDescription(professional.getCompanyDescription());
        dto.setCertified(professional.getCertified());
        dto.setCertificationUrl(professional.getCertificationUrl());
        dto.setSpecialty(professional.getSpecialty());
        dto.setPlanId(professional.getPlan() != null ? professional.getPlan().getId() : null);
        dto.setInterestIds(professional.getInterests() != null ?
            professional.getInterests().stream().map(c -> c.getId()).collect(Collectors.toList()) : null);
        return dto;
    }
}
