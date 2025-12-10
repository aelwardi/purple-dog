package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for shipping rates response from Shippo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingRatesDTO {

    private String shipmentId;
    private List<ShippingRate> rates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingRate {
        private String rateId;
        private String provider;
        private String serviceLevelName;
        private String serviceLevelToken;
        private BigDecimal amount;
        private String currency;
        private Integer estimatedDays;
        private String durationTerms;
        private Boolean available;
        private String errorMessage;
    }
}

