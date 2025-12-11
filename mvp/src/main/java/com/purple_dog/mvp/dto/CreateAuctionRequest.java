package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionRequest {

    private Long productId;

    private BigDecimal desiredPrice;

    private BigDecimal customStartingPrice;

    private BigDecimal bidIncrement;
}
