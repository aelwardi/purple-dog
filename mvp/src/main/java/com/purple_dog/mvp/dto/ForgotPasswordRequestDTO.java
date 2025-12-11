package com.purple_dog.mvp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;
}

