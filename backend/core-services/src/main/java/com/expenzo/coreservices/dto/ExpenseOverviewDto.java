package com.expenzo.coreservices.dto;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseOverviewDto {

    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal bill;
    private BigDecimal averagePerDay;
}
