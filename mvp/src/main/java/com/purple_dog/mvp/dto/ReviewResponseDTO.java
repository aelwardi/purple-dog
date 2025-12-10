package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour réponse d'un avis (ADMIN ONLY)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer rating;
    private String title;
    private String comment;
    private ReviewStatus status;
    private String adminResponse;
    private Long reviewedByAdminId;
    private String reviewedByAdminName;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

