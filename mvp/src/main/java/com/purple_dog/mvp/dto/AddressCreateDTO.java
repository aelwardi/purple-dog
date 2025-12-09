package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreateDTO {

    @NotNull(message = "Person ID is required")
    private Long personId;

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Street is required")
    private String street;

    private String complement;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    private Boolean isDefault;
}
