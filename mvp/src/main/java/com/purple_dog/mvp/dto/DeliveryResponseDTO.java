package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponseDTO {

    private Long id;
    private Long orderId;
    private String orderReference;
    private CarrierDTO carrier;
    private String trackingNumber;
    private String trackingUrl;
    private DeliveryStatus status;
    private String labelUrl;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String buyerName;
    private String deliveryAddress;

    private String productTitle;
    private String sellerName;
}

