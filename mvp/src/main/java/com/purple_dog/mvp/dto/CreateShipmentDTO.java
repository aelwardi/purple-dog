package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating a shipment with Shippo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "From name is required")
    private String fromName;

    @NotBlank(message = "From street is required")
    private String fromStreet;

    @NotBlank(message = "From city is required")
    private String fromCity;

    @NotBlank(message = "From zip is required")
    private String fromZip;

    @NotBlank(message = "From country is required")
    private String fromCountry;

    private String fromState;
    private String fromPhone;
    private String fromEmail;

    private String toName;
    private String toStreet;
    private String toCity;
    private String toZip;
    private String toCountry;
    private String toState;
    private String toPhone;
    private String toEmail;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private BigDecimal length;

    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private BigDecimal width;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private BigDecimal height;

    private String carrierAccount;
    private String serviceLevelToken;

    private String labelFileType;
    private Boolean includeInsurance;
    private BigDecimal insuranceAmount;
    private String insuranceCurrency;
}

