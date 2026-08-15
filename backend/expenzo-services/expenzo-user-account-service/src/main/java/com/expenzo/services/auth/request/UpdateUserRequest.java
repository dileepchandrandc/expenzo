package com.expenzo.services.auth.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 5, message = "Country code must not exceed 5 characters")
    private String countryCode;

    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNumber;
}
