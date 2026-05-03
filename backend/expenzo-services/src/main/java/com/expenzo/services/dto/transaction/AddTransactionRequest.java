package com.expenzo.services.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.expenzo.services.enums.PaymentChannel;
import com.expenzo.services.enums.TransactionType;

import lombok.*;

@Getter
@Setter
public class AddTransactionRequest {
    private TransactionType type;
    private BigDecimal amount;
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private PaymentChannel sourceType;
    private Integer sourceId;
    private PaymentChannel destType;
    private Integer destId;
    private TransactionMetaData metaData; //This can be used to mention the extra data required to form the transaction like expense category for expense
}
