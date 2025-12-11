package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureDTO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Boolean active;
    private long plansCount;
}

