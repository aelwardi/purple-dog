package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Notification;
import com.purple_dog.mvp.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :id")
    void markAsRead(@Param("id") Long id, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.createdAt < :before")
    int deleteOldNotifications(@Param("userId") Long userId, @Param("before") LocalDateTime before);

    @Query("SELECT n FROM Notification n WHERE n.emailSent = false AND n.user.email IS NOT NULL")
    List<Notification> findPendingEmailNotifications();
}

