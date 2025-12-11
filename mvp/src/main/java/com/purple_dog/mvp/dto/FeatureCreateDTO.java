package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureCreateDTO {

    @NotBlank(message = "Feature name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Feature code is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Code must contain only uppercase letters and underscores")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;

    private Boolean active;
}

