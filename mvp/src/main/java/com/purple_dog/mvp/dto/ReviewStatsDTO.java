package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour statistiques des avis (ADMIN ONLY)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatsDTO {

    private Long totalReviews;
    private Long pendingReviews;
    private Long approvedReviews;
    private Long rejectedReviews;

    private Double averageRating;

    private Long rating5Count;
    private Long rating4Count;
    private Long rating3Count;
    private Long rating2Count;
    private Long rating1Count;
}

