package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProfessionalResponseDTO extends PersonResponseDTO {
    private String companyName;
    private String siret;
    private String tvaNumber;
    private String website;
    private String companyDescription;
    private Boolean certified;
    private String certificationUrl;
    private String specialty;
    private Long planId;
    private List<Long> interestIds;
}

