package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierCreateDTO {

    @NotBlank(message = "Carrier name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Carrier code is required")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must contain only uppercase letters, numbers and underscores")
    @Size(max = 20, message = "Code must not exceed 20 characters")
    private String code;

    @Size(max = 200, message = "Logo URL must not exceed 200 characters")
    private String logo;

    @Size(max = 200, message = "API endpoint must not exceed 200 characters")
    private String apiEndpoint;

    @Size(max = 200, message = "Tracking URL pattern must not exceed 200 characters")
    private String trackingUrlPattern; // Ex: "https://track.carrier.com?num={trackingNumber}"

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", message = "Base price must be positive")
    private BigDecimal basePrice;

    private Boolean active;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}

