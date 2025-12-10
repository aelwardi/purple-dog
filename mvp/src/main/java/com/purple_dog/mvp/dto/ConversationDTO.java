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
public class ConversationDTO {

    private Long id;
    private Long otherUserId;
    private String otherUserFirstName;
    private String otherUserRole;
    private String otherUserProfilePicture;
    private Long orderId;
    private Boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private MessageDTO lastMessage;
    private long unreadCount;
}

