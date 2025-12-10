package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDTO {
    private Long id;
    private Long productId;
    private BigDecimal desiredPrice;
    private BigDecimal startingPrice;
    private BigDecimal reservePrice;
    private BigDecimal currentPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Boolean reservePriceMet;
    private Long currentWinnerId;
    private Integer totalBids;
    private LocalDateTime createdAt;
}
