package com.purple_dog.mvp.services;

import com.purple_dog.mvp.config.JwtTokenProvider;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.PasswordResetTokenRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final IndividualService individualService;
    private final ProfessionalService professionalService;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSenderService emailSenderService;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.password-reset.expiration:3600000}") // 1 hour default
    private long passwordResetExpirationMs;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            Person person = personRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            person.setLastLoginAt(LocalDateTime.now());
            personRepository.save(person);

            UserInfoDTO userInfo = mapPersonToUserInfo(person);

            log.info("User {} logged in successfully", request.getEmail());

            return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpirationMs / 1000)
                    .user(userInfo)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for email: {}", request.getEmail());
            throw new InvalidOperationException("Invalid email or password");
        }
    }

    public LoginResponseDTO registerIndividual(RegisterIndividualDTO request) {
        log.info("Registering individual: {}", request.getEmail());

        if (personRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        individualService.createIndividual(request);

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        Person person = personRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                null,
                userDetailsService.loadUserByUsername(request.getEmail()).getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserInfoDTO userInfo = mapPersonToUserInfo(person);

        log.info("Individual {} registered successfully", request.getEmail());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(userInfo)
                .build();
    }

    public LoginResponseDTO registerProfessional(RegisterProfessionalDTO request) {
        log.info("Registering professional: {}", request.getEmail());

        if (personRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        professionalService.createProfessional(request);

        Person person = personRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                null,
                userDetailsService.loadUserByUsername(request.getEmail()).getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserInfoDTO userInfo = mapPersonToUserInfo(person);

        log.info("Professional {} registered successfully", request.getEmail());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(userInfo)
                .build();
    }

    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        log.info("Refreshing token");

        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidOperationException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getUsernameFromToken(refreshToken);
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                userDetailsService.loadUserByUsername(email).getAuthorities()
        );

        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        UserInfoDTO userInfo = mapPersonToUserInfo(person);

        log.info("Token refreshed for user: {}", email);

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(userInfo)
                .build();
    }

    public UserInfoDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapPersonToUserInfo(person);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out");
    }

    public void forgotPassword(String email) {
        log.info("Forgot password request for email: {}", email);

        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun compte trouvé avec cet email"));

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByPerson(person);

        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Create and save token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .person(person)
                .expiryDate(LocalDateTime.now().plusSeconds(passwordResetExpirationMs / 1000))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send email with reset link
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String emailSubject = "Réinitialisation de votre mot de passe - Purple Dog";
        String emailText = String.format(
            "Bonjour %s,\n\n" +
            "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
            "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n%s\n\n" +
            "Ce lien expirera dans 1 heure.\n\n" +
            "Si vous n'avez pas fait cette demande, ignorez cet email.\n\n" +
            "Cordialement,\n" +
            "L'équipe Purple Dog",
            person.getFirstName(), resetLink
        );

        emailSenderService.sendSimpleEmail(person.getEmail(), emailSubject, emailText);

        log.info("Password reset email sent to: {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        log.info("Reset password request with token");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidOperationException("Token invalide ou expiré"));

        if (resetToken.getUsed()) {
            throw new InvalidOperationException("Ce lien a déjà été utilisé");
        }

        if (resetToken.isExpired()) {
            throw new InvalidOperationException("Ce lien a expiré");
        }

        // Update password
        Person person = resetToken.getPerson();
        person.setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", person.getEmail());
    }

    private UserInfoDTO mapPersonToUserInfo(Person person) {
        return UserInfoDTO.builder()
                .id(person.getId())
                .email(person.getEmail())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .phone(person.getPhone())
                .role(person.getRole())
                .accountStatus(person.getAccountStatus())
                .profilePicture(person.getProfilePicture())
                .emailVerified(person.getEmailVerified())
                .phoneVerified(person.getPhoneVerified())
                .build();
    }
}

