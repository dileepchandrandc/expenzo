package com.expenzo.services.dto.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddDebitCardRequest {
    private Integer bankAccountId;
}
