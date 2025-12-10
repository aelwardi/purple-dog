package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.PaymentMethod;
import com.purple_dog.mvp.entities.PaymentStatus;
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
public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String stripeChargeId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime refundedAt;

    private String buyerName;
    private String buyerEmail;
}

