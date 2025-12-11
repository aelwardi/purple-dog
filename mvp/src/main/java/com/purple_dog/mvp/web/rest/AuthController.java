package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "APIs for user authentication and registration")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login request for email: {}", request.getEmail());
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Register individual endpoint
     * POST /api/auth/register/individual
     */
    @PostMapping("/register/individual")
    public ResponseEntity<LoginResponseDTO> registerIndividual(@Valid @RequestBody RegisterIndividualDTO request) {
        log.info("Register individual request for email: {}", request.getEmail());
        LoginResponseDTO response = authService.registerIndividual(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Register professional endpoint
     * POST /api/auth/register/professional
     */
    @PostMapping("/register/professional")
    public ResponseEntity<LoginResponseDTO> registerProfessional(@Valid @RequestBody RegisterProfessionalDTO request) {
        log.info("Register professional request for email: {}", request.getEmail());
        LoginResponseDTO response = authService.registerProfessional(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Refresh token endpoint
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        log.info("Refresh token request");
        LoginResponseDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user endpoint
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser() {
        log.info("Get current user request");
        UserInfoDTO user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Logout endpoint
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("Logout request");
        authService.logout();
        return ResponseEntity.ok().build();
    }

    /**
     * Forgot password endpoint
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new MessageResponseDTO("Un email de réinitialisation a été envoyé à votre adresse"));
    }

    /**
     * Reset password endpoint
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        log.info("Reset password request with token");
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponseDTO("Mot de passe réinitialisé avec succès"));
    }
}
