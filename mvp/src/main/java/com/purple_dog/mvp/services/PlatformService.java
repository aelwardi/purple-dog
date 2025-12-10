package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PlatformRepository;
import com.purple_dog.mvp.dto.PlatformResponseDTO;
import com.purple_dog.mvp.dto.PlatformUpdateDTO;
import com.purple_dog.mvp.entities.Platform;
import com.purple_dog.mvp.entities.PlatformStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformResponseDTO getPlatformInfo() {
        log.info("Fetching platform information");

        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        return mapToResponseDTO(platform);
    }

    public PlatformResponseDTO updatePlatform(PlatformUpdateDTO dto) {
        log.info("Updating platform information");

        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        if (dto.getDescription() != null) {
            platform.setDescription(dto.getDescription());
        }
        if (dto.getEmail() != null) {
            platform.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            platform.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            platform.setAddress(dto.getAddress());
        }
        if (dto.getWebsite() != null) {
            platform.setWebsite(dto.getWebsite());
        }
        if (dto.getLogoUrl() != null) {
            platform.setLogoUrl(dto.getLogoUrl());
        }
        if (dto.getPlatformCommission() != null) {
            platform.setPlatformCommission(dto.getPlatformCommission());
        }
        if (dto.getTermsOfServiceUrl() != null) {
            platform.setTermsOfServiceUrl(dto.getTermsOfServiceUrl());
        }
        if (dto.getPrivacyPolicyUrl() != null) {
            platform.setPrivacyPolicyUrl(dto.getPrivacyPolicyUrl());
        }
        if (dto.getCompanyRegistration() != null) {
            platform.setCompanyRegistration(dto.getCompanyRegistration());
        }
        if (dto.getVatNumber() != null) {
            platform.setVatNumber(dto.getVatNumber());
        }

        platform = platformRepository.save(platform);
        log.info("Platform information updated successfully");

        return mapToResponseDTO(platform);
    }

    public void updateStatistics(Long totalUsers, Long totalProducts, Long totalTransactions, BigDecimal totalRevenue) {
        log.info("Updating platform statistics");

        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        if (totalUsers != null) {
            platform.setTotalUsers(totalUsers);
        }
        if (totalProducts != null) {
            platform.setTotalProducts(totalProducts);
        }
        if (totalTransactions != null) {
            platform.setTotalTransactions(totalTransactions);
        }
        if (totalRevenue != null) {
            platform.setTotalRevenue(totalRevenue);
        }

        platformRepository.save(platform);
        log.info("Platform statistics updated successfully");
    }

    public void incrementUserCount() {
        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        platform.setTotalUsers(platform.getTotalUsers() + 1);
        platformRepository.save(platform);
    }

    public void incrementProductCount() {
        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        platform.setTotalProducts(platform.getTotalProducts() + 1);
        platformRepository.save(platform);
    }

    public void addTransaction(BigDecimal amount) {
        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        platform.setTotalTransactions(platform.getTotalTransactions() + 1);
        platform.setTotalRevenue(platform.getTotalRevenue().add(amount));
        platformRepository.save(platform);
    }

    public void updateStatus(PlatformStatus status) {
        log.info("Updating platform status to: {}", status);

        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(this::createDefaultPlatform);

        platform.setStatus(status);
        platformRepository.save(platform);

        log.info("Platform status updated successfully");
    }

    private Platform createDefaultPlatform() {
        log.info("Creating default platform instance");

        Platform platform = Platform.builder()
                .name("Purple Dog")
                .description("Plateforme de trading d'objets de valeur")
                .email("contact@purpledog.com")
                .phone("+33 1 23 45 67 89")
                .website("https://purpledog.com")
                .totalUsers(0L)
                .totalProducts(0L)
                .totalTransactions(0L)
                .totalRevenue(BigDecimal.ZERO)
                .platformCommission(new BigDecimal("5.00"))
                .status(PlatformStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return platformRepository.save(platform);
    }

    private PlatformResponseDTO mapToResponseDTO(Platform platform) {
        return PlatformResponseDTO.builder()
                .id(platform.getId())
                .name(platform.getName())
                .description(platform.getDescription())
                .email(platform.getEmail())
                .phone(platform.getPhone())
                .address(platform.getAddress())
                .website(platform.getWebsite())
                .logoUrl(platform.getLogoUrl())
                .totalUsers(platform.getTotalUsers())
                .totalProducts(platform.getTotalProducts())
                .totalTransactions(platform.getTotalTransactions())
                .status(platform.getStatus())
                .createdAt(platform.getCreatedAt())
                .updatedAt(platform.getUpdatedAt())
                .build();
    }
}

