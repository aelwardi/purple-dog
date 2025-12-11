package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressUpdateDTO {

    @Size(max = 50, message = "Label must not exceed 50 characters")
    private String label;

    @Size(max = 200, message = "Street must not exceed 200 characters")
    private String street;

    @Size(max = 200, message = "Complement must not exceed 200 characters")
    private String complement;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(min = 5, max = 10, message = "Postal code must be between 5 and 10 characters")
    private String postalCode;

    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    private Boolean isDefault;
}

