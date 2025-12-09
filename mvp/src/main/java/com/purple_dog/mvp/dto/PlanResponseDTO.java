package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.PlanType;
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
public class PlanResponseDTO {

    private Long id;
    private PlanType type;
    private String name;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal annualPrice;
    private Integer maxListings;
    private Integer maxPhotosPerListing;
    private Boolean featuredListings;
    private Boolean prioritySupport;
    private Boolean analyticsAccess;
    private Boolean customBranding;
    private BigDecimal commissionRate;
    private Boolean active;
    private List<FeatureDTO> features;
    private long subscribersCount;

    private BigDecimal annualSavings;
}

