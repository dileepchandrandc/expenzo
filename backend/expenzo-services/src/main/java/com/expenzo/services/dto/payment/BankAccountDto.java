package com.expenzo.services.dto.payment;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {

    private Integer id;
    private BankDto bank;

    private String nickName;
}
