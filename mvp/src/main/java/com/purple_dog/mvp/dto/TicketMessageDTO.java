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
public class TicketMessageDTO {

    private Long id;
    private Long ticketId;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private String content;
    private Boolean isStaffReply;
    private LocalDateTime createdAt;
}

