package com.expenzo.services.paymentcard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardResponse {

    private String id;
    private String userId;
    private String bankAccountId;
    private String cardNumber;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BigDecimal creditLimit;
    private Integer billingDate;
    private String nickName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean isActive;
}
