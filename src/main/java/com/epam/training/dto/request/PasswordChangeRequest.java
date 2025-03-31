package com.epam.training.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequest {

    @NotNull(message = "Please enter your username")
    private String username;

    @NotNull(message = "Please enter your old password")
    private String oldPassword;

    @NotNull(message = "Please enter your new password")
    private String newPassword;
}