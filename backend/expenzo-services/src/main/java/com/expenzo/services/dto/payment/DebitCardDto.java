package com.expenzo.services.dto.payment;

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
