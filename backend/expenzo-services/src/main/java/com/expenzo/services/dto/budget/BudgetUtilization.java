package com.expenzo.services.dto.budget;

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
public class BudgetUtilization {

    private Integer budgetId;
    private String budgetName;
    private BigDecimal budgetLimit;
    private BigDecimal totalSpent;
    private List<BudgetCategoryUtilization> utilizations;
    private BigDecimal untrackedSpent;
    private BigDecimal uncategorizedSpent;
    private Float budgetUsage;
    private Float untrackedUsage;
    private Float uncategorizedUsage;
}
