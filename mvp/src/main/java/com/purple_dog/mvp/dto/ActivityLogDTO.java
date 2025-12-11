package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDTO {
    private Long id;
    private String activityType; // USER_REGISTERED, PRODUCT_LISTED, ORDER_PLACED, TICKET_CREATED, etc.
    private String description;
    private String userEmail;
    private String userName;
    private Long userId;
    private String entityType; // USER, PRODUCT, ORDER, TICKET, etc.
    private Long entityId;
    private LocalDateTime timestamp;
    private String severity; // INFO, WARNING, CRITICAL
}
