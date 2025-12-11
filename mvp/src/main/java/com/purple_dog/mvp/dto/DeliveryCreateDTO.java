package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryCreateDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Carrier ID is required")
    private Long carrierId;

    private String trackingNumber;

    private LocalDateTime estimatedDeliveryDate;

    private String notes;
}

