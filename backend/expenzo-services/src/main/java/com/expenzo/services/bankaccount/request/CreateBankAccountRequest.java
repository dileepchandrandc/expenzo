package com.expenzo.services.bankaccount.request;

import com.expenzo.services.bankaccount.enums.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankAccountRequest {

    @NotBlank(message = "Bank id is mandatory")
    private String bankId;

    @NotNull(message = "Account type is mandatory")
    private AccountType accountType;

    @NotBlank(message = "Account number is mandatory")
    @Size(max = 50, message = "Account number must not exceed 50 characters")
    private String accountNumber;

    @Size(max = 50, message = "Nick name must not exceed 50 characters")
    private String nickName;
}
