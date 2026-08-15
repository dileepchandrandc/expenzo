package com.expenzo.services.bankaccount.dto;

import java.time.OffsetDateTime;

import com.expenzo.services.bankaccount.enums.AccountType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountResponse {

    private String id;
    private String userId;
    private BankResponse bank;
    private AccountType accountType;
    private String accountNumber;
    private String nickName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean isActive;
}
