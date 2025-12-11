package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ProductCondition;
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
public class AlertCreateDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String keywords;

    @DecimalMin(value = "0.0", message = "Minimum price must be positive")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", message = "Maximum price must be positive")
    private BigDecimal maxPrice;

    private ProductCondition condition;

    private Boolean emailNotification;

    private Boolean inAppNotification;
}

