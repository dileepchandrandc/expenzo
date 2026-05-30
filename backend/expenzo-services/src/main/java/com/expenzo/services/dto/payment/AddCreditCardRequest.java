package com.expenzo.services.dto.payment;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCreditCardRequest {
    private Integer bankAccountId;
    private BigDecimal limit;
    private BigDecimal currentOutStanding;
    private int billingDate;
}
