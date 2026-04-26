package com.expenzo.transactionservices.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.expenzo.transactionservices.enums.PaymentChannel;

import lombok.*;

@Getter
@Setter
public class AddTransactionRequest {
    private BigDecimal amount;
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private PaymentChannel sourceType;
    private Integer sourceId;
    private PaymentChannel destType;
    private Integer destId;
}
