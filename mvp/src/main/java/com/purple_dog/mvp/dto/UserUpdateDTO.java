package com.purple_dog.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePicture;
    private String bio;
}

