package com.purple_dog.mvp.web;

import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.ReviewStatus;
import com.purple_dog.mvp.services.PlatformReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PlatformReviewController {

    private final PlatformReviewService reviewService;

    /**
     * Créer un avis sur la plateforme
     * Accessible à tout utilisateur authentifié
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewCreateDTO dto) {
        log.info("Request to create review from user: {}", dto.getUserId());
        ReviewResponseDTO response = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== ENDPOINTS ADMIN ONLY ====================

    /**
     * Récupérer tous les avis
     * ADMIN ONLY
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        log.info("Request to get all reviews (ADMIN)");
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * Récupérer les avis en attente de modération
     * ADMIN ONLY
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<ReviewResponseDTO>> getPendingReviews() {
        log.info("Request to get pending reviews (ADMIN)");
        List<ReviewResponseDTO> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * Récupérer les avis par statut
     * ADMIN ONLY
     */
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByStatus(@PathVariable ReviewStatus status) {
        log.info("Request to get reviews with status: {} (ADMIN)", status);
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByStatus(status);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Récupérer un avis par ID
     * ADMIN ONLY
     */
    @GetMapping("/admin/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long reviewId) {
        log.info("Request to get review: {} (ADMIN)", reviewId);
        ReviewResponseDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    /**
     * Récupérer les avis d'un utilisateur
     * ADMIN ONLY
     */
    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(@PathVariable Long userId) {
        log.info("Request to get reviews for user: {} (ADMIN)", userId);
        List<ReviewResponseDTO> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Modérer un avis (Approuver/Rejeter)
     * ADMIN ONLY
     */
    @PostMapping("/admin/{reviewId}/moderate")
    public ResponseEntity<ReviewResponseDTO> moderateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewModerationDTO dto) {

        log.info("Request to moderate review: {} by admin: {} (ADMIN)", reviewId, dto.getAdminId());
        ReviewResponseDTO response = reviewService.moderateReview(reviewId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer un avis
     * ADMIN ONLY
     */
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        log.info("Request to delete review: {} (ADMIN)", reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer les statistiques des avis
     * ADMIN ONLY
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<ReviewStatsDTO> getReviewStatistics() {
        log.info("Request to get review statistics (ADMIN)");
        ReviewStatsDTO stats = reviewService.getReviewStatistics();
        return ResponseEntity.ok(stats);
    }
}

