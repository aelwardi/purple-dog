package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {

    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String firstName;

    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String lastName;

    @Email(message = "L'email doit être valide")
    private String email;

    @Size(min = 10, max = 20, message = "Le téléphone doit contenir entre 10 et 20 caractères")
    private String phone;

    private String bio;

    private String profilePicture;

    // For professionals
    private String companyName;

    private String siret;
}

