package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour modération d'un avis par un admin (ADMIN ONLY)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewModerationDTO {

    @NotNull(message = "Admin ID is required")
    private Long adminId;

    @NotNull(message = "Status is required")
    private ReviewStatus status; // APPROVED ou REJECTED

    private String adminResponse; // Réponse optionnelle de l'admin
}

