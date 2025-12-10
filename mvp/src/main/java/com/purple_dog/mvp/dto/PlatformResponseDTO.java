package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.PlatformStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String address;
    private String website;
    private String logoUrl;

    private Long totalUsers;
    private Long totalProducts;
    private Long totalTransactions;

    private PlatformStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

