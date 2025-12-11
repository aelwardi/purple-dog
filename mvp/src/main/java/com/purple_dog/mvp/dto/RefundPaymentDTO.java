package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for refunding a payment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentDTO {

    @NotBlank(message = "Payment intent ID is required")
    private String paymentIntentId;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount; // Optional: for partial refund

    private String reason; // duplicate, fraudulent, requested_by_customer
}

