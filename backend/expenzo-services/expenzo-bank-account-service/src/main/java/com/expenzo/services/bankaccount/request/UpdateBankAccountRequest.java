package com.expenzo.services.bankaccount.request;

import com.expenzo.services.bankaccount.enums.AccountType;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBankAccountRequest {

    private String bankId;

    private AccountType accountType;

    @Size(max = 50, message = "Account number must not exceed 50 characters")
    private String accountNumber;

    @Size(max = 50, message = "Nick name must not exceed 50 characters")
    private String nickName;
}
