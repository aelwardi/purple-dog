package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.TicketPriority;
import com.purple_dog.mvp.entities.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketResponseDTO {

    private Long id;
    private String ticketNumber;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private Long assignedAdminId;
    private String assignedAdminName;
    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private long messageCount;
    private TicketMessageDTO lastMessage;
    private java.util.List<TicketMessageDTO> ticketMessages;
}

