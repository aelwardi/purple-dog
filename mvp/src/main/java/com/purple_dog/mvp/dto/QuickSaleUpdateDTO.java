package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickSaleUpdateDTO {

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal fixedPrice;

    private Boolean acceptOffers;

    @DecimalMin(value = "0.01", message = "Minimum offer price must be greater than 0")
    private BigDecimal minimumOfferPrice;

    private Boolean isAvailable;
}

