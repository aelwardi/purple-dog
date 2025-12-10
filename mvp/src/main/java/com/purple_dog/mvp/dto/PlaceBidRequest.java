package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBidRequest {
    private Long auctionId;
    private Long bidderId; // Professional ID
    private BigDecimal amount;
    private BigDecimal maxAmount; // Pour enchère automatique (optionnel)
}
