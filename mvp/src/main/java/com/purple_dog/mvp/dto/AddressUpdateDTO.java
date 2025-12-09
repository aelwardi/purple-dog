package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressUpdateDTO {

    private String label;
    private String street;
    private String complement;
    private String city;
    private String postalCode;
    private String country;
    private Boolean isDefault;
}
