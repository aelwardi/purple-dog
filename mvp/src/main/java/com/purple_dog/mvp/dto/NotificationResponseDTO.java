package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private String linkUrl;
    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean emailSent;
    private LocalDateTime emailSentAt;
    private String metadata;
    private LocalDateTime createdAt;
}

