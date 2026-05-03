package com.expenzo.services.dto.payment;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDto {

    private Integer id;
    private BankAccountDto bankAccount;

    private BigDecimal limit;
    private BigDecimal currentOutStanding;
    private int billingDate;
}
