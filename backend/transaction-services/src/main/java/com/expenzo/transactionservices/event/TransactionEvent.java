package com.expenzo.transactionservices.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.expenzo.transactionservices.dto.TransactionMetaData;
import com.expenzo.transactionservices.enums.EventAction;
import com.expenzo.transactionservices.enums.PaymentChannel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEvent {

    private EventAction action;

    private Integer id;
    private BigDecimal amount;
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private PaymentChannel sourceType;
    private Integer sourceId;
    private PaymentChannel destType;
    private Integer destId;

    private TransactionMetaData metaData;
}
