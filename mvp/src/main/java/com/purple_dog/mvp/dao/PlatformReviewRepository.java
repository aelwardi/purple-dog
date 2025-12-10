package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.PlatformReview;
import com.purple_dog.mvp.entities.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformReviewRepository extends JpaRepository<PlatformReview, Long> {

    // Tous les avis (ADMIN ONLY)
    List<PlatformReview> findAllByOrderByCreatedAtDesc();

    // Avis par statut (ADMIN ONLY)
    List<PlatformReview> findByStatusOrderByCreatedAtDesc(ReviewStatus status);

    // Avis en attente de modération (ADMIN ONLY)
    @Query("SELECT r FROM PlatformReview r WHERE r.status = 'PENDING' ORDER BY r.createdAt ASC")
    List<PlatformReview> findPendingReviews();

    // Avis d'un utilisateur (ADMIN ONLY)
    List<PlatformReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Vérifier si un utilisateur a déjà laissé un avis
    boolean existsByUserId(Long userId);

    // Avis avec détails (ADMIN ONLY)
    @Query("SELECT r FROM PlatformReview r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.reviewedBy WHERE r.id = :id")
    Optional<PlatformReview> findByIdWithDetails(@Param("id") Long id);

    // Compter les avis par statut (ADMIN ONLY)
    long countByStatus(ReviewStatus status);

    // Statistiques (ADMIN ONLY)
    @Query("SELECT AVG(r.rating) FROM PlatformReview r WHERE r.status = 'APPROVED'")
    Double getAverageRatingApproved();

    @Query("SELECT COUNT(r) FROM PlatformReview r WHERE r.status = 'APPROVED' AND r.rating = :rating")
    long countByRating(@Param("rating") Integer rating);
}

