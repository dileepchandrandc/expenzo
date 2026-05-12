package com.expenzo.services.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.expenzo.services.dto.payment.PaymentChannelDto;

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
public class ExpenseDto {

    private Integer id;
    private BigDecimal amount;
    private LocalDateTime spentOn;
    private String title;
    private String description;
    private ExpenseCategoryDto category;
    private PaymentChannelDto paymentSource;
}
