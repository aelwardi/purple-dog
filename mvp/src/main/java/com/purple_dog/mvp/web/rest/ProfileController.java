package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.ChangePasswordDTO;
import com.purple_dog.mvp.dto.MessageResponseDTO;
import com.purple_dog.mvp.dto.UpdateProfileDTO;
import com.purple_dog.mvp.dto.UserInfoDTO;
import com.purple_dog.mvp.services.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile Management", description = "APIs for managing user profiles")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Get current user profile
     * GET /api/profile
     */
    @Operation(summary = "Get current user profile")
    @GetMapping
    public ResponseEntity<UserInfoDTO> getCurrentProfile() {
        log.info("GET /profile - Getting current user profile");
        UserInfoDTO profile = profileService.getCurrentProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * Update current user profile
     * PUT /api/profile
     */
    @Operation(summary = "Update current user profile")
    @PutMapping
    public ResponseEntity<UserInfoDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO updateDTO) {
        log.info("PUT /profile - Updating profile");
        UserInfoDTO updatedProfile = profileService.updateProfile(updateDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Change password
     * PUT /api/profile/change-password
     */
    @Operation(summary = "Change password for current user")
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponseDTO> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("PUT /profile/change-password - Changing password");
        profileService.changePassword(changePasswordDTO);
        return ResponseEntity.ok(new MessageResponseDTO("Mot de passe modifié avec succès"));
    }

    /**
     * Delete current user account
     * DELETE /api/profile
     */
    @Operation(summary = "Delete current user account")
    @DeleteMapping
    public ResponseEntity<MessageResponseDTO> deleteAccount() {
        log.info("DELETE /profile - Deleting account");
        profileService.deleteAccount();
        return ResponseEntity.ok(new MessageResponseDTO("Compte supprimé avec succès"));
    }
}

