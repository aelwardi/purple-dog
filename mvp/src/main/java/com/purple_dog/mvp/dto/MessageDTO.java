package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderFirstName;
    private String senderRole;
    private String content;
    private Boolean wasFiltered;
    private Boolean isBlocked;
    private String blockReason;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private Boolean isMine;
}

