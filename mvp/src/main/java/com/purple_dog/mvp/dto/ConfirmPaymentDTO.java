package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for confirming a payment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentDTO {

    @NotBlank(message = "Payment intent ID is required")
    private String paymentIntentId;
}

