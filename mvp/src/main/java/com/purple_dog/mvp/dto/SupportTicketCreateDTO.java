package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketCreateDTO {

    @NotBlank(message = "Subject is required")
    @Size(min = 5, max = 200, message = "Subject must be between 5 and 200 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    private TicketPriority priority;

    private String category;
}

