package com.expenzo.services.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Old password is mandatory")
    private String oldPassword;

    @NotBlank(message = "New password is mandatory")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "New password must contain at least 1 lowercase letter, 1 uppercase letter, 1 digit, and be at least 8 characters long"
    )
    @Size(max = 128, message = "New password must not exceed 128 characters")
    private String newPassword;
}
