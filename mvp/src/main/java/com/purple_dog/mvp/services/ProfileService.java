package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.ChangePasswordDTO;
import com.purple_dog.mvp.dto.UpdateProfileDTO;
import com.purple_dog.mvp.dto.UserInfoDTO;
import com.purple_dog.mvp.entities.Individual;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.Professional;
import com.purple_dog.mvp.entities.UserRole;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get current user profile
     */
    public UserInfoDTO getCurrentProfile() {
        Person person = getCurrentUser();
        return mapToUserInfoDTO(person);
    }

    /**
     * Update current user profile
     */
    public UserInfoDTO updateProfile(UpdateProfileDTO updateDTO) {
        Person person = getCurrentUser();

        log.info("Updating profile for user: {}", person.getEmail());

        // Update common fields
        if (updateDTO.getFirstName() != null) {
            person.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            person.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getPhone() != null) {
            person.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getBio() != null) {
            person.setBio(updateDTO.getBio());
        }
        if (updateDTO.getProfilePicture() != null) {
            person.setProfilePicture(updateDTO.getProfilePicture());
        }

        // Update email if changed and not already used
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(person.getEmail())) {
            if (personRepository.findByEmail(updateDTO.getEmail()).isPresent()) {
                throw new InvalidOperationException("Cet email est déjà utilisé");
            }
            person.setEmail(updateDTO.getEmail());
        }

        // Update professional fields if applicable
        if (person.getRole() == UserRole.PROFESSIONAL && person instanceof Professional) {
            Professional professional = (Professional) person;
            if (updateDTO.getCompanyName() != null) {
                professional.setCompanyName(updateDTO.getCompanyName());
            }
            if (updateDTO.getSiret() != null) {
                professional.setSiret(updateDTO.getSiret());
            }
        }

        Person updatedPerson = personRepository.save(person);
        log.info("Profile updated successfully for user: {}", updatedPerson.getEmail());

        return mapToUserInfoDTO(updatedPerson);
    }

    /**
     * Change password for current user
     */
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Person person = getCurrentUser();

        log.info("Changing password for user: {}", person.getEmail());

        // Verify current password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), person.getPassword())) {
            throw new InvalidOperationException("Le mot de passe actuel est incorrect");
        }

        // Verify new password and confirmation match
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new InvalidOperationException("Les nouveaux mots de passe ne correspondent pas");
        }

        // Verify new password is different from current
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), person.getPassword())) {
            throw new InvalidOperationException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        // Update password
        person.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        personRepository.save(person);

        log.info("Password changed successfully for user: {}", person.getEmail());
    }

    /**
     * Delete current user account
     */
    public void deleteAccount() {
        Person person = getCurrentUser();

        log.info("Deleting account for user: {}", person.getEmail());

        personRepository.delete(person);

        log.info("Account deleted successfully for user: {}", person.getEmail());
    }

    /**
     * Get current authenticated user
     */
    private Person getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return personRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    /**
     * Map Person to UserInfoDTO
     */
    private UserInfoDTO mapToUserInfoDTO(Person person) {
        UserInfoDTO.UserInfoDTOBuilder builder = UserInfoDTO.builder()
                .id(person.getId())
                .email(person.getEmail())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .phone(person.getPhone())
                .role(person.getRole())
                .accountStatus(person.getAccountStatus())
                .profilePicture(person.getProfilePicture())
                .emailVerified(person.getEmailVerified())
                .phoneVerified(person.getPhoneVerified());

        // Add professional specific fields
        if (person instanceof Professional) {
            Professional professional = (Professional) person;
            builder.companyName(professional.getCompanyName());
            builder.siret(professional.getSiret());
        }

        return builder.build();
    }
}

