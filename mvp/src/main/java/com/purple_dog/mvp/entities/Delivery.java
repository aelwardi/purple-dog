package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @Column(unique = true)
    private String shippoShipmentId;

    @Column(unique = true)
    private String shippoTransactionId;

    @Column(unique = true)
    private String shippoTrackingNumber;

    private String trackingNumber;
    private String trackingStatus;
    private String trackingUrlProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    private String carrierName;
    private String serviceLevelName;
    private String serviceLevelToken;

    private String labelUrl;
    private String labelFileType;
    private String commercialInvoiceUrl;

    @Column(length = 1000)
    private String fromAddress;

    @Column(length = 1000)
    private String toAddress;

    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    // Cost information
    private BigDecimal shippingCost;
    private String currency;

    // Dates
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime inTransitAt;

    // Additional information
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON format for additional Shippo data

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

