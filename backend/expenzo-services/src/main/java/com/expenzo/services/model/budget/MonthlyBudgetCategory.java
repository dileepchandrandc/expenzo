package com.expenzo.services.model.budget;

import java.math.BigDecimal;

import com.expenzo.services.model.expense.ExpenseCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "monthly_budget_expense_category")
@IdClass(MonthlyBudgetCategoryId.class)
public class MonthlyBudgetCategory {

    @Id
    @ManyToOne
    @JoinColumn(name = "budget_id")
    private MonthlyBudget budget;

    @Id
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;

    private Integer userId;
    private BigDecimal spendLimit;
}
