package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.PaymentMethod;
import com.purple_dog.mvp.entities.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String stripeToken; // Token de carte Stripe

    private String stripeCustomerId; // Si client existant
}

