package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Payment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long id;
    private String stripePaymentIntentId;
    private BigDecimal amount;
    private String currency;
    private Payment.PaymentStatus status;
    private Payment.PaymentType type;
    private Long userId;
    private String userEmail;
    private Long orderId;
    private String description;
    private String receiptUrl;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private String failureMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

