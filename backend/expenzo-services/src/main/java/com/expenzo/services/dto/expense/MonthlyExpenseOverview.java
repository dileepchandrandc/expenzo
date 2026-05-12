package com.expenzo.services.dto.expense;

import java.math.BigDecimal;

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
public class MonthlyExpenseOverview {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal avgSpentPerDay;
}
