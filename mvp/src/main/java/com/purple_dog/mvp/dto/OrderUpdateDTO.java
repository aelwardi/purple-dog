package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.OrderStatus;
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
public class OrderUpdateDTO {

    private OrderStatus status;

    @Positive(message = "Shipping cost must be positive")
    private BigDecimal shippingCost;

    @Positive(message = "Platform fee must be positive")
    private BigDecimal platformFee;

    private Long shippingAddressId;

    private Long billingAddressId;
}
