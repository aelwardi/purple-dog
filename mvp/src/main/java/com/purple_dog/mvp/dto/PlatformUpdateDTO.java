package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour mise à jour de la plateforme (ADMIN ONLY)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformUpdateDTO {

    private String description;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;
    private String address;
    private String website;
    private String logoUrl;

    @DecimalMin(value = "0.0", message = "Commission must be at least 0%")
    @DecimalMax(value = "100.0", message = "Commission cannot exceed 100%")
    private BigDecimal platformCommission;

    private String termsOfServiceUrl;
    private String privacyPolicyUrl;
    private String companyRegistration;
    private String vatNumber;
}

