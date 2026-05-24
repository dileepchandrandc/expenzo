package com.expenzo.services.dto.expense;

import java.math.BigDecimal;
import java.util.List;

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
public class DailySpendingTrendDto {

    private Integer day;
    private BigDecimal totalAmountSpent;
    private List<ExpenseDto> expenses;
}
