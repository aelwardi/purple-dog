package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DocumentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVerificationDTO {

    @NotNull(message = "Status is required")
    private DocumentStatus status;

    private String rejectionReason;
}

