package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCreateDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;
}

