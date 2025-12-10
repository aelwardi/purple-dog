package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    private Long id;
    private Long auctionId;
    private Long bidderId;
    private String bidderName;
    private BigDecimal amount;
    private BigDecimal maxAmount;
    private Boolean isAutoBid;
    private Boolean isWinning;
    private LocalDateTime bidDate;
}
