package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickSaleCreateDTO {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Fixed price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal fixedPrice;
    
    private Boolean acceptOffers;
    
    @DecimalMin(value = "0.01", message = "Minimum offer price must be greater than 0")
    private BigDecimal minimumOfferPrice;
}

