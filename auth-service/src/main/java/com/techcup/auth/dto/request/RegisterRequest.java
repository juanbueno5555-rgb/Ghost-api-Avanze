package com.techcup.auth.dto.request;

import com.techcup.auth.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class RegisterRequest {
    @NotBlank @Email
    String email;

    @NotBlank @Size(min = 8)
    String password;

    @NotBlank
    String fullName;

    @NotBlank
    String documentId;

    @NotBlank
    String phone;

    UserType userType;

    String invitationCode;
}
