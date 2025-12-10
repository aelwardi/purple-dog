package com.purple_dog.mvp.dto;

import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private AccountStatus accountStatus;
    private String profilePicture;
    private Boolean emailVerified;
    private Boolean phoneVerified;
}

