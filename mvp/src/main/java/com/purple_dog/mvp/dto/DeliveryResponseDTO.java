package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DeliveryStatus;
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
public class DeliveryResponseDTO {

    private Long id;
    private Long orderId;
    private String orderReference;

    private String shippoShipmentId;
    private String shippoTransactionId;
    private String shippoTrackingNumber;

    private CarrierDTO carrier;
    private String carrierName;
    private String serviceLevelName;

    private String trackingNumber;
    private String trackingStatus;
    private String trackingUrl;

    private DeliveryStatus status;
    private String labelUrl;
    private String commercialInvoiceUrl;

    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime inTransitAt;

    private BigDecimal shippingCost;
    private String currency;

    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String buyerName;
    private String deliveryAddress;

    private String productTitle;
    private String sellerName;
}

