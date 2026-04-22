package com.expenzo.coreservices.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {

    private Integer id;

    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDateTime spentOn;

    private BankAccountDto bankAccount;
    private CreditCardDto creditCard;
    private DebitCardDto debitCard;
}
