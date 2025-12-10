package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ProductCondition;
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
public class AlertUpdateDTO {

    private String keywords;

    @DecimalMin(value = "0.0", message = "Minimum price must be positive")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", message = "Maximum price must be positive")
    private BigDecimal maxPrice;

    private ProductCondition condition;

    private Boolean active;

    private Boolean emailNotification;

    private Boolean inAppNotification;
}

