package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponseDTO {

    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String keywords;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private ProductCondition condition;
    private Boolean active;
    private Boolean emailNotification;
    private Boolean inAppNotification;
    private LocalDateTime createdAt;
    private LocalDateTime lastTriggeredAt;
}

