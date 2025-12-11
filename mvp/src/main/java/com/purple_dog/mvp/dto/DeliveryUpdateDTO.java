package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DeliveryStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryUpdateDTO {

    private String trackingNumber;

    private DeliveryStatus status;

    @Size(max = 200, message = "Label URL must not exceed 200 characters")
    private String labelUrl;

    private LocalDateTime estimatedDeliveryDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}

