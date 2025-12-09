package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.IndividualRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.IndividualCreateDTO;
import com.purple_dog.mvp.dto.IndividualResponseDTO;
import com.purple_dog.mvp.dto.IndividualUpdateDTO;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.Individual;
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
public class IndividualService {

    private final IndividualRepository individualRepository;
    private final PersonRepository personRepository;

    public IndividualResponseDTO createIndividual(IndividualCreateDTO dto) {
        log.info("Creating individual with email: {}", dto.getEmail());

        if (personRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        if (dto.getPhone() != null && personRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
        }

        Individual individual = Individual.builder()
                .email(dto.getEmail())
                .password(dto.getPassword()) // TODO: Encoder le mot de passe avec BCrypt
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .role(UserRole.INDIVIDUAL)
                .accountStatus(AccountStatus.PENDING_VERIFICATION)
                .profilePicture(dto.getProfilePicture())
                .bio(dto.getBio())
                .emailVerified(false)
                .phoneVerified(false)
                .identityVerified(false)
                .maxSalesPerMonth(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Individual saved = individualRepository.save(individual);
        log.info("Individual created successfully with ID: {}", saved.getId());

        return mapToResponseDTO(saved);
    }

    public IndividualResponseDTO getIndividualById(Long id) {
        log.info("Fetching individual with ID: {}", id);
        Individual individual = individualRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Individual not found with ID: " + id));
        return mapToResponseDTO(individual);
    }

    public List<IndividualResponseDTO> getAllIndividuals() {
        log.info("Fetching all individuals");
        return individualRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<IndividualResponseDTO> getIndividualsByVerificationStatus(Boolean verified) {
        log.info("Fetching individuals by verification status: {}", verified);
        return individualRepository.findByIdentityVerified(verified).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public IndividualResponseDTO updateIndividual(Long id, IndividualUpdateDTO dto) {
        log.info("Updating individual with ID: {}", id);
        Individual individual = individualRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Individual not found with ID: " + id));

        if (dto.getFirstName() != null) individual.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) individual.setLastName(dto.getLastName());
        if (dto.getPhone() != null) {
            if (!dto.getPhone().equals(individual.getPhone()) && personRepository.existsByPhone(dto.getPhone())) {
                throw new DuplicateResourceException("Phone number already exists: " + dto.getPhone());
            }
            individual.setPhone(dto.getPhone());
        }
        if (dto.getProfilePicture() != null) individual.setProfilePicture(dto.getProfilePicture());
        if (dto.getBio() != null) individual.setBio(dto.getBio());
        if (dto.getIdentityDocumentUrl() != null) individual.setIdentityDocumentUrl(dto.getIdentityDocumentUrl());

        individual.setUpdatedAt(LocalDateTime.now());
        Individual updated = individualRepository.save(individual);
        log.info("Individual updated successfully with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public void deleteIndividual(Long id) {
        log.info("Deleting individual with ID: {}", id);
        if (!individualRepository.existsById(id)) {
            throw new ResourceNotFoundException("Individual not found with ID: " + id);
        }
        individualRepository.deleteById(id);
        log.info("Individual deleted successfully with ID: {}", id);
    }

    public IndividualResponseDTO verifyIdentity(Long id) {
        log.info("Verifying identity for individual with ID: {}", id);
        Individual individual = individualRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Individual not found with ID: " + id));

        individual.setIdentityVerified(true);
        individual.setAccountStatus(AccountStatus.ACTIVE);
        individual.setUpdatedAt(LocalDateTime.now());

        Individual updated = individualRepository.save(individual);
        log.info("Identity verified for individual with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public IndividualResponseDTO updateAccountStatus(Long id, AccountStatus status) {
        log.info("Updating account status for individual with ID: {} to {}", id, status);
        Individual individual = individualRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Individual not found with ID: " + id));

        individual.setAccountStatus(status);
        individual.setUpdatedAt(LocalDateTime.now());

        Individual updated = individualRepository.save(individual);
        log.info("Account status updated for individual with ID: {}", updated.getId());

        return mapToResponseDTO(updated);
    }

    public long countIndividuals() {
        return individualRepository.count();
    }

    public long countVerifiedIndividuals() {
        return individualRepository.countByIdentityVerified(true);
    }

    private IndividualResponseDTO mapToResponseDTO(Individual individual) {
        IndividualResponseDTO dto = new IndividualResponseDTO();
        dto.setId(individual.getId());
        dto.setEmail(individual.getEmail());
        dto.setFirstName(individual.getFirstName());
        dto.setLastName(individual.getLastName());
        dto.setPhone(individual.getPhone());
        dto.setRole(individual.getRole());
        dto.setAccountStatus(individual.getAccountStatus());
        dto.setProfilePicture(individual.getProfilePicture());
        dto.setBio(individual.getBio());
        dto.setEmailVerified(individual.getEmailVerified());
        dto.setPhoneVerified(individual.getPhoneVerified());
        dto.setCreatedAt(individual.getCreatedAt());
        dto.setUpdatedAt(individual.getUpdatedAt());
        dto.setLastLoginAt(individual.getLastLoginAt());
        dto.setIdentityVerified(individual.getIdentityVerified());
        dto.setIdentityDocumentUrl(individual.getIdentityDocumentUrl());
        dto.setMaxSalesPerMonth(individual.getMaxSalesPerMonth());
        return dto;
    }
}

