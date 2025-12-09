package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDTO {

    @NotNull(message = "Buyer ID is required")
    private Long buyerId;

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    private Long auctionId;

    private Long quickSaleId;

    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be positive")
    private BigDecimal productPrice;

    @Positive(message = "Shipping cost must be positive")
    private BigDecimal shippingCost;

    @Positive(message = "Platform fee must be positive")
    private BigDecimal platformFee;

    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    @NotNull(message = "Billing address ID is required")
    private Long billingAddressId;
}
