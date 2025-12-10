package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for purchasing a shipping label
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLabelDTO {

    @NotBlank(message = "Rate ID is required")
    private String rateId;

    private String labelFileType;

    private Boolean async;
}

