package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DocumentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadDTO {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    private String description;
}

