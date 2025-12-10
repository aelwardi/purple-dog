package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    @NotBlank(message = "L'ancien mot de passe est requis")
    private String currentPassword;

    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères")
    private String newPassword;

    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String confirmPassword;
}

