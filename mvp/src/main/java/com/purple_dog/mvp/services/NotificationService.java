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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

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
        log.info("Notification created successfully with id: {}", notification.getId());

        return mapToResponseDTO(notification);
    }

    /**
     * Récupérer une notification par ID
     */
    public NotificationResponseDTO getNotificationById(Long notificationId) {
        log.info("Fetching notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        return mapToResponseDTO(notification);
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        log.info("Fetching notifications for user: {}", userId);

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);

        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les notifications récentes (dernières 24h)
     */
    public List<NotificationResponseDTO> getRecentNotifications(Long userId) {
        log.info("Fetching recent notifications for user: {}", userId);

        LocalDateTime since = LocalDateTime.now().minusDays(1);
        return notificationRepository.findRecentByUserId(userId, since).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marquer une notification comme lue
     */
    public NotificationResponseDTO markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getIsRead()) {
            notificationRepository.markAsRead(notificationId, LocalDateTime.now());
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        log.info("Notification marked as read");
        return mapToResponseDTO(notification);
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    public int markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);

        int count = notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
        log.info("{} notifications marked as read", count);

        return count;
    }

    /**
     * Compter les notifications non lues
     */
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        notificationRepository.delete(notification);
        log.info("Notification deleted successfully");
    }

    /**
     * Supprimer les anciennes notifications (plus de 30 jours)
     */
    public int deleteOldNotifications(Long userId) {
        log.info("Deleting old notifications for user: {}", userId);

        LocalDateTime before = LocalDateTime.now().minusDays(30);
        int count = notificationRepository.deleteOldNotifications(userId, before);

        log.info("{} old notifications deleted", count);
        return count;
    }

    /**
     * Notification nouvelle enchère
     */
    public void createBidNotification(Long userId, String productTitle, String bidderName, String amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.BID_PLACED)
                .title("Nouvelle enchère")
                .message(String.format("%s a placé une enchère de %s € sur %s", bidderName, amount, productTitle))
                .build();

        createNotification(dto);
    }

    /**
     * Notification enchère dépassée
     */
    public void createOutbidNotification(Long userId, String productTitle, String amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.BID_OUTBID)
                .title("Vous avez été surenchéri")
                .message(String.format("Votre enchère sur %s a été dépassée. Nouvelle enchère : %s €", productTitle, amount))
                .build();

        createNotification(dto);
    }

    /**
     * Notification enchère gagnée
     */
    public void createAuctionWonNotification(Long userId, String productTitle, String finalPrice) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.BID_WON)
                .title("Félicitations ! Vous avez remporté l'enchère")
                .message(String.format("Vous avez remporté l'enchère pour %s au prix de %s €", productTitle, finalPrice))
                .build();

        createNotification(dto);
    }

    /**
     * Notification offre reçue
     */
    public void createOfferReceivedNotification(Long userId, String productTitle, String buyerName, String amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.OFFER_RECEIVED)
                .title("Nouvelle offre reçue")
                .message(String.format("%s a fait une offre de %s € pour %s", buyerName, amount, productTitle))
                .build();

        createNotification(dto);
    }

    /**
     * Notification offre acceptée
     */
    public void createOfferAcceptedNotification(Long userId, String productTitle, String amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.OFFER_ACCEPTED)
                .title("Votre offre a été acceptée !")
                .message(String.format("Votre offre de %s € pour %s a été acceptée", amount, productTitle))
                .build();

        createNotification(dto);
    }

    /**
     * Notification offre rejetée
     */
    public void createOfferRejectedNotification(Long userId, String productTitle) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.OFFER_REJECTED)
                .title("Offre refusée")
                .message(String.format("Votre offre pour %s a été refusée", productTitle))
                .build();

        createNotification(dto);
    }

    /**
     * Notification commande créée
     */
    public void createOrderNotification(Long userId, String orderNumber, String totalAmount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.ORDER_CREATED)
                .title("Commande confirmée")
                .message(String.format("Votre commande %s d'un montant de %s € a été créée", orderNumber, totalAmount))
                .build();

        createNotification(dto);
    }

    /**
     * Notification paiement reçu
     */
    public void createPaymentReceivedNotification(Long userId, String orderNumber, String amount) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.PAYMENT_RECEIVED)
                .title("Paiement reçu")
                .message(String.format("Le paiement de %s € pour la commande %s a été reçu", amount, orderNumber))
                .build();

        createNotification(dto);
    }

    /**
     * Notification expédition
     */
    public void createShippingNotification(Long userId, String orderNumber, String trackingNumber) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.ITEM_SHIPPED)
                .title("Votre colis a été expédié")
                .message(String.format("Votre commande %s a été expédiée. Numéro de suivi : %s", orderNumber, trackingNumber))
                .build();

        createNotification(dto);
    }

    /**
     * Notification livraison
     */
    public void createDeliveryNotification(Long userId, String orderNumber) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.ITEM_DELIVERED)
                .title("Colis livré")
                .message(String.format("Votre commande %s a été livrée", orderNumber))
                .build();

        createNotification(dto);
    }

    /**
     * Notification nouveau message
     */
    public void createMessageNotification(Long userId, String senderName) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.MESSAGE_RECEIVED)
                .title("Nouveau message")
                .message(String.format("Vous avez reçu un message de %s", senderName))
                .build();

        createNotification(dto);
    }

    /**
     * Notification produit validé
     */
    public void createProductValidatedNotification(Long userId, String productTitle) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.PRODUCT_VALIDATED)
                .title("Produit approuvé")
                .message(String.format("Votre produit %s a été validé et est maintenant visible", productTitle))
                .build();

        createNotification(dto);
    }

    /**
     * Notification correspondance alerte
     */
    public void createAlertMatchNotification(Long userId, Long productId, String productTitle, Long alertId) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.ALERT_MATCH)
                .title("Nouvelle correspondance d'alerte")
                .message(String.format("Un nouveau produit correspond à votre alerte : %s", productTitle))
                .linkUrl("/products/" + productId)
                .metadata("{\"alertId\":" + alertId + ",\"productId\":" + productId + "}")
                .build();

        createNotification(dto);
    }

    /**
     * Notification réponse ticket
     */
    public void createTicketResponseNotification(Long userId, String ticketNumber) {
        NotificationCreateDTO dto = NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.TICKET_RESPONSE)
                .title("Réponse à votre ticket")
                .message(String.format("Une réponse a été apportée à votre ticket %s", ticketNumber))
                .build();

        createNotification(dto);
    }

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

