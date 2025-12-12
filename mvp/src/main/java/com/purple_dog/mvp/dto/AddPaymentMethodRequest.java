package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding Stripe payment method to professional
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentMethodRequest {
    
    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;
}
