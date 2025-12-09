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
public class FavoriteDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private String productTitle;
    private String productDescription;
    private BigDecimal productPrice;
    private String productMainImage;
    private String sellerName;
    private LocalDateTime createdAt;
}

