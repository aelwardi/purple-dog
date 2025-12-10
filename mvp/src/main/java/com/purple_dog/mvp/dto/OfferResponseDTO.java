package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.OfferStatus;
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
public class OfferResponseDTO {

    private Long id;
    private Long quickSaleId;
    private String productTitle;
    private BigDecimal fixedPrice;
    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private BigDecimal amount;
    private String message;
    private OfferStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    private Long sellerId;
    private String sellerName;
}

