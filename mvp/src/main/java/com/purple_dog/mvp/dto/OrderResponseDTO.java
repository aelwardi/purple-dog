package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.OrderStatus;
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
public class OrderResponseDTO {

    private Long id;
    private String orderNumber;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private Long auctionId;
    private Long quickSaleId;
    private BigDecimal productPrice;
    private BigDecimal shippingCost;
    private BigDecimal platformFee;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Long shippingAddressId;
    private Long billingAddressId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
