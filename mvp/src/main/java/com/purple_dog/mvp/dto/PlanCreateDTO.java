package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.PlanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanCreateDTO {

    @NotNull(message = "Plan type is required")
    private PlanType type;

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.0", message = "Monthly price must be positive")
    private BigDecimal monthlyPrice;

    @NotNull(message = "Annual price is required")
    @DecimalMin(value = "0.0", message = "Annual price must be positive")
    private BigDecimal annualPrice;

    @Min(value = 0, message = "Max listings must be positive")
    private Integer maxListings; // null = unlimited

    @Min(value = 1, message = "Max photos must be at least 1")
    private Integer maxPhotosPerListing;

    private Boolean featuredListings;

    private Boolean prioritySupport;

    private Boolean analyticsAccess;

    private Boolean customBranding;

    @NotNull(message = "Commission rate is required")
    @DecimalMin(value = "0.0", message = "Commission rate must be positive")
    @DecimalMax(value = "100.0", message = "Commission rate must not exceed 100%")
    private BigDecimal commissionRate;

    private Boolean active;

    private List<Long> featureIds;
}

