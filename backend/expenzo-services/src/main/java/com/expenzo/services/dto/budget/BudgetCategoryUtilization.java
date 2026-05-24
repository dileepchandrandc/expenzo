package com.expenzo.services.dto.budget;

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
public class BudgetCategoryUtilization {

    private Integer categoryId;
    private String categoryName;
    private BigDecimal spendLimit;
    private BigDecimal totalSpent;
    private float budgetUsage;
    private boolean isPartOfBudget;
}
