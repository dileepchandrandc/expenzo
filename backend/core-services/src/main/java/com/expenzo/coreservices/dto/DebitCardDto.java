package com.expenzo.coreservices.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDto {

    private Integer id;
    private BankAccountDto bankAccount;
}
