package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.TicketPriority;
import com.purple_dog.mvp.entities.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketUpdateDTO {

    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private String category;
    private Long assignedAdminId;
}

