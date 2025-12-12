package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AdminRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dao.PlatformRepository;
import com.purple_dog.mvp.dao.PlatformReviewRepository;
import com.purple_dog.mvp.dto.*;
import com.purple_dog.mvp.entities.*;
import com.purple_dog.mvp.exceptions.DuplicateResourceException;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlatformReviewService {

    private final PlatformReviewRepository reviewRepository;
    private final PlatformRepository platformRepository;
    private final PersonRepository personRepository;
    private final AdminRepository adminRepository;
    private final InAppNotificationService inAppNotificationService;

    /**
     * Créer un avis (Utilisateur authentifié)
     */
    public ReviewResponseDTO createReview(ReviewCreateDTO dto) {
        log.info("Creating review from user: {}", dto.getUserId());

        Person user = personRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        if (reviewRepository.existsByUserId(dto.getUserId())) {
            throw new DuplicateResourceException("User has already submitted a review");
        }

        // Récupérer ou créer la plateforme par défaut
        Platform platform = platformRepository.findPlatformInstance()
                .orElseGet(() -> {
                    log.info("No platform found, creating default platform");
                    Platform newPlatform = Platform.builder()
                            .name("Purple Dog")
                            .description("Plateforme de vente d'objets d'occasion")
                            .email("contact@purple-dog.com")
                            .phone("+33 1 23 45 67 89")
                            .address("France")
                            .status(PlatformStatus.ACTIVE)
                            .build();
                    return platformRepository.save(newPlatform);
                });

        PlatformReview review = PlatformReview.builder()
                .platform(platform)
                .user(user)
                .rating(dto.getRating())
                .title(dto.getTitle())
                .comment(dto.getComment())
                .status(ReviewStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        review = reviewRepository.save(review);
        log.info("Review created successfully with id: {} - Status: PENDING", review.getId());

        // Créer une notification pour les admins
        try {
            // Récupérer le premier admin (ou tous les admins)
            List<Admin> admins = adminRepository.findAll();
            for (Admin admin : admins) {
                NotificationCreateDTO notificationDTO = NotificationCreateDTO.builder()
                        .userId(admin.getId())
                        .type(NotificationType.SYSTEM)
                        .title("📝 Nouvel avis en attente de modération")
                        .message(String.format("Un nouvel avis a été soumis par %s %s",
                                user.getFirstName(), user.getLastName()))
                        .linkUrl("/admin/reviews")
                        .build();
                inAppNotificationService.createNotification(notificationDTO);
            }
            log.info("✅ Admins notified about new review");
        } catch (Exception e) {
            log.error("❌ Failed to notify admins about new review: {}", e.getMessage());
        }

        return mapToResponseDTO(review);
    }

    /**
     * Récupérer tous les avis (ADMIN ONLY)
     */
    public List<ReviewResponseDTO> getAllReviews() {
        log.info("Fetching all reviews (ADMIN)");

        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les avis en attente de modération (ADMIN ONLY)
     */
    public List<ReviewResponseDTO> getPendingReviews() {
        log.info("Fetching pending reviews (ADMIN)");

        return reviewRepository.findPendingReviews().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les avis par statut (ADMIN ONLY)
     */
    public List<ReviewResponseDTO> getReviewsByStatus(ReviewStatus status) {
        log.info("Fetching reviews with status: {} (ADMIN)", status);

        return reviewRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un avis par ID (ADMIN ONLY)
     */
    public ReviewResponseDTO getReviewById(Long reviewId) {
        log.info("Fetching review: {} (ADMIN)", reviewId);

        PlatformReview review = reviewRepository.findByIdWithDetails(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        return mapToResponseDTO(review);
    }

    /**
     * Récupérer les avis d'un utilisateur (ADMIN ONLY)
     */
    public List<ReviewResponseDTO> getUserReviews(Long userId) {
        log.info("Fetching reviews for user: {} (ADMIN)", userId);

        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Modérer un avis (ADMIN ONLY)
     */
    public ReviewResponseDTO moderateReview(Long reviewId, ReviewModerationDTO dto) {
        log.info("Moderating review: {} by admin: {}", reviewId, dto.getAdminId());

        // Vérifier que l'admin existe
        Admin admin = adminRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + dto.getAdminId()));

        // Récupérer l'avis
        PlatformReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Vérifier que l'avis est en attente
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new InvalidOperationException("Only pending reviews can be moderated");
        }

        // Mettre à jour le statut
        review.setStatus(dto.getStatus());
        review.setAdminResponse(dto.getAdminResponse());
        review.setReviewedBy(admin);
        review.setReviewedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        review = reviewRepository.save(review);
        log.info("Review {} moderated with status: {}", reviewId, dto.getStatus());

        // Créer une notification pour l'utilisateur
        try {
            String title = dto.getStatus() == ReviewStatus.APPROVED
                    ? "✅ Votre avis a été approuvé"
                    : "❌ Votre avis n'a pas été approuvé";

            String message = dto.getStatus() == ReviewStatus.APPROVED
                    ? "Votre avis sur Purple Dog a été approuvé et est maintenant visible publiquement"
                    : "Votre avis sur Purple Dog n'a pas été approuvé. " +
                      (dto.getAdminResponse() != null ? "Raison : " + dto.getAdminResponse() : "");

            NotificationCreateDTO notificationDTO = NotificationCreateDTO.builder()
                    .userId(review.getUser().getId())
                    .type(NotificationType.SYSTEM)
                    .title(title)
                    .message(message)
                    .linkUrl("/feedback")
                    .build();

            inAppNotificationService.createNotification(notificationDTO);
            log.info("✅ User {} notified about review moderation", review.getUser().getId());
        } catch (Exception e) {
            log.error("❌ Failed to notify user about review moderation: {}", e.getMessage());
        }

        return mapToResponseDTO(review);
    }

    /**
     * Supprimer un avis (ADMIN ONLY)
     */
    public void deleteReview(Long reviewId) {
        log.info("Deleting review: {} (ADMIN)", reviewId);

        PlatformReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        reviewRepository.delete(review);
        log.info("Review deleted successfully");
    }

    public ReviewStatsDTO getReviewStatistics() {
        log.info("Fetching review statistics (ADMIN)");

        long totalReviews = reviewRepository.count();
        long pendingReviews = reviewRepository.countByStatus(ReviewStatus.PENDING);
        long approvedReviews = reviewRepository.countByStatus(ReviewStatus.APPROVED);
        long rejectedReviews = reviewRepository.countByStatus(ReviewStatus.REJECTED);

        Double averageRating = reviewRepository.getAverageRatingApproved();

        long rating5 = reviewRepository.countByRating(5);
        long rating4 = reviewRepository.countByRating(4);
        long rating3 = reviewRepository.countByRating(3);
        long rating2 = reviewRepository.countByRating(2);
        long rating1 = reviewRepository.countByRating(1);

        return ReviewStatsDTO.builder()
                .totalReviews(totalReviews)
                .pendingReviews(pendingReviews)
                .approvedReviews(approvedReviews)
                .rejectedReviews(rejectedReviews)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .rating5Count(rating5)
                .rating4Count(rating4)
                .rating3Count(rating3)
                .rating2Count(rating2)
                .rating1Count(rating1)
                .build();
    }

    // Méthodes privées

    private ReviewResponseDTO mapToResponseDTO(PlatformReview review) {
        Person user = review.getUser();
        Admin reviewedBy = review.getReviewedBy();

        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .status(review.getStatus())
                .adminResponse(review.getAdminResponse())
                .reviewedByAdminId(reviewedBy != null ? reviewedBy.getId() : null)
                .reviewedByAdminName(reviewedBy != null ?
                        reviewedBy.getFirstName() + " " + reviewedBy.getLastName() : null)
                .reviewedAt(review.getReviewedAt())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}

