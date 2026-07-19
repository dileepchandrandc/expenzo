package com.expenzo.services.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccount {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String countryCode;
    private String mobile;
}
