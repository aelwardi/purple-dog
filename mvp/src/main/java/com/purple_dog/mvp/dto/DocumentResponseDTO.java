package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.DocumentStatus;
import com.purple_dog.mvp.entities.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {

    private Long id;
    private Long personId;
    private DocumentType documentType;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String description;
    private DocumentStatus status;
    private String rejectionReason;
    private LocalDateTime verifiedAt;
    private Long verifiedById;
    private String verifiedByName;
    private LocalDateTime uploadedAt;
}

