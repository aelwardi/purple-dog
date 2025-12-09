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
public class AddressResponseDTO {

    private Long id;
    private String label;
    private String street;
    private String complement;
    private String city;
    private String postalCode;
    private String country;
    private Boolean isDefault;
    private LocalDateTime createdAt;

    private String fullAddress;
}

