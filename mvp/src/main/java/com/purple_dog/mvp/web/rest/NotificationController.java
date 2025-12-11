package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.NotificationCreateDTO;
import com.purple_dog.mvp.dto.NotificationResponseDTO;
import com.purple_dog.mvp.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Notification Management", description = "APIs for managing user notifications")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Créer une notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody NotificationCreateDTO dto) {
        log.info("Request to create notification for user: {}", dto.getUserId());
        NotificationResponseDTO response = notificationService.createNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer une notification par ID
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long notificationId) {
        log.info("Request to get notification: {}", notificationId);
        NotificationResponseDTO notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(notification);
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        log.info("Request to get notifications for user: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer les notifications non lues
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long userId) {
        log.info("Request to get unread notifications for user: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer les notifications récentes (24h)
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<NotificationResponseDTO>> getRecentNotifications(@PathVariable Long userId) {
        log.info("Request to get recent notifications for user: {}", userId);
        List<NotificationResponseDTO> notifications = notificationService.getRecentNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marquer une notification comme lue
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long notificationId) {
        log.info("Request to mark notification as read: {}", notificationId);
        NotificationResponseDTO response = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(response);
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable Long userId) {
        log.info("Request to mark all notifications as read for user: {}", userId);
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Compter les notifications non lues
     */
    @GetMapping("/user/{userId}/count-unread")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        log.info("Request to count unread notifications for user: {}", userId);
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        log.info("Request to delete notification: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprimer les anciennes notifications (>30 jours)
     */
    @DeleteMapping("/user/{userId}/old")
    public ResponseEntity<Integer> deleteOldNotifications(@PathVariable Long userId) {
        log.info("Request to delete old notifications for user: {}", userId);
        int count = notificationService.deleteOldNotifications(userId);
        return ResponseEntity.ok(count);
    }
}
