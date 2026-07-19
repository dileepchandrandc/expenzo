package com.expenzo.services.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must contain at least 1 lowercase letter, 1 uppercase letter, 1 digit, and be at least 8 characters long"
    )
    @Size(max = 128, message = "Password must not exceed 128 characters")
    private String password;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 5, message = "Country code must not exceed 5 characters")
    private String countryCode;

    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNumber;
}
