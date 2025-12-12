package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.NotificationRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.NotificationCreateDTO;
import com.purple_dog.mvp.dto.NotificationResponseDTO;
import com.purple_dog.mvp.entities.Notification;
import com.purple_dog.mvp.entities.NotificationType;
import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les notifications in-app (différent de NotificationService qui gère les emails)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InAppNotificationService {

    private final NotificationRepository notificationRepository;
    private final PersonRepository personRepository;

    /**
     * Créer une notification
     */
    public NotificationResponseDTO createNotification(NotificationCreateDTO dto) {
        log.info("Creating notification for user: {}", dto.getUserId());

        Person user = personRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Notification notification = Notification.builder()
                .user(user)
                .type(dto.getType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .linkUrl(dto.getLinkUrl())
                .isRead(false)
                .emailSent(false)
                .metadata(dto.getMetadata())
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);
        log.info("✅ Notification created with id: {}", notification.getId());

        return mapToResponseDTO(notification);
    }

    /**
     * Créer une notification pour un match d'alerte
     */
    public NotificationResponseDTO createAlertMatchNotification(Long userId, Long productId, String productTitle, Long alertId) {
        log.info("Creating alert match notification for user: {} and product: {}", userId, productId);

        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.ALERT_MATCH)
                .title("🔔 Alerte : Nouveau produit correspondant !")
                .message(String.format("Un nouveau produit correspond à votre alerte : %s", productTitle))
                .linkUrl("/product/" + productId)
                .metadata(String.format("{\"productId\": %d, \"alertId\": %d}", productId, alertId))
                .build();

        return createNotification(dto);
    }

    /**
     * Créer une notification pour une nouvelle enchère
     */
    public NotificationResponseDTO createBidPlacedNotification(Long sellerId, String productTitle, String bidderName, Double bidAmount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(sellerId)
                .type(NotificationType.BID_PLACED)
                .title("💰 Nouvelle enchère !")
                .message(String.format("%s a placé une enchère de %.2f € sur %s", bidderName, bidAmount, productTitle))
                .build();

        return createNotification(dto);
    }

    /**
     * Créer une notification pour enchère dépassée
     */
    public NotificationResponseDTO createBidOutbidNotification(Long userId, String productTitle, Double newBidAmount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.BID_OUTBID)
                .title("⚠️ Enchère dépassée")
                .message(String.format("Votre enchère sur %s a été dépassée. Nouvelle enchère : %.2f €", productTitle, newBidAmount))
                .build();

        return createNotification(dto);
    }

    /**
     * Créer une notification pour commande créée
     */
    public NotificationResponseDTO createOrderNotification(Long sellerId, String orderNumber, Double amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(sellerId)
                .type(NotificationType.ORDER_CREATED)
                .title("📦 Nouvelle commande !")
                .message(String.format("Vous avez reçu une nouvelle commande %s pour un montant de %.2f €", orderNumber, amount))
                .linkUrl("/dashboard?tab=orders")
                .build();

        return createNotification(dto);
    }

    /**
     * Créer une notification pour paiement reçu
     */
    public NotificationResponseDTO createPaymentReceivedNotification(Long sellerId, String orderNumber, Double amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(sellerId)
                .type(NotificationType.PAYMENT_RECEIVED)
                .title("✅ Paiement reçu")
                .message(String.format("Le paiement de %.2f € pour la commande %s a été reçu", amount, orderNumber))
                .linkUrl("/dashboard?tab=orders")
                .build();

        return createNotification(dto);
    }

    /**
     * Créer une notification pour ticket support
     */
    public NotificationResponseDTO createTicketResponseNotification(Long userId, String ticketNumber) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.TICKET_RESPONSE)
                .title("💬 Nouvelle réponse à votre ticket")
                .message(String.format("Vous avez reçu une réponse pour le ticket %s", ticketNumber))
                .linkUrl("/support")
                .build();

        return createNotification(dto);
    }

    /**
     * Récupérer une notification par ID
     */
    @Transactional(readOnly = true)
    public NotificationResponseDTO getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        return mapToResponseDTO(notification);
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les notifications non lues
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les notifications récentes (24h)
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getRecentNotifications(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Notification> notifications = notificationRepository.findRecentByUserId(userId, since);
        return notifications.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marquer une notification comme lue
     */
    public NotificationResponseDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
            log.info("✅ Notification {} marked as read", notificationId);
        }

        return mapToResponseDTO(notification);
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    public int markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        LocalDateTime now = LocalDateTime.now();
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
        log.info("✅ Marked {} notifications as read for user {}", unreadNotifications.size(), userId);

        return unreadNotifications.size();
    }

    /**
     * Compter les notifications non lues
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notificationRepository.delete(notification);
        log.info("🗑️ Notification {} deleted", notificationId);
    }

    /**
     * Supprimer les anciennes notifications (>30 jours)
     */
    public int deleteOldNotifications(Long userId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int deleted = notificationRepository.deleteOldNotifications(userId, thirtyDaysAgo);
        // deleteOldNotifications already deletes via @Modifying query; return deleted count
        log.info("🗑️ Deleted {} old notifications for user {}", deleted, userId);

        return deleted;
    }

    /**
     * Mapper Notification vers NotificationResponseDTO
     */
    private NotificationResponseDTO mapToResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .linkUrl(notification.getLinkUrl())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .emailSent(notification.getEmailSent())
                .emailSentAt(notification.getEmailSentAt())
                .metadata(notification.getMetadata())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
