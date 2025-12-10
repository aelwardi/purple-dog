package com.purple_dog.mvp.dto;

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
public class QuickSaleResponseDTO {

    private Long id;
    private Long productId;
    private String productTitle;
    private String productDescription;
    private String productMainImage;
    private BigDecimal fixedPrice;
    private Boolean acceptOffers;
    private BigDecimal minimumOfferPrice;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime soldAt;

    private Long sellerId;
    private String sellerName;

    private long offersCount;
    private long pendingOffersCount;
}

