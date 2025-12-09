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
public class ProfessionalUpdateDTO extends UserUpdateDTO {
    private String companyName;
    private String website;
    private String companyDescription;
    private String specialty;
    private String certificationUrl;
    private List<Long> interestIds;
}

