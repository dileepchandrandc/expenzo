package com.expenzo.services.dto.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBankAccountRequest {
    private Integer bankId;
    private String nickName;
}
