package com.purple_dog.mvp.services;

import com.purple_dog.mvp.config.JwtTokenProvider;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
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

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

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

