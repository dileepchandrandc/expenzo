package com.expenzo.services.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.expenzo.services.dto.budget.BudgetCategoryUtilization;
import com.expenzo.services.dto.budget.BudgetUtilization;
import com.expenzo.services.repository.query.SqlQueries;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BudgetRepository {

    private final JdbcTemplate jdbcTemplate;

    public BudgetUtilization getBudgetSummaryReport(Integer userId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        BudgetUtilization budgetUtilization = new BudgetUtilization();
        List<BudgetCategoryUtilization> utilizations = jdbcTemplate.query(SqlQueries.Budget.FETCH_BUDGET_REPORT, (resultSet, rowNum) -> {
            if (budgetUtilization.getBudgetId() == null) {
                budgetUtilization.setBudgetId(resultSet.getInt("budget_id"));
            }
            if (budgetUtilization.getBudgetName() == null) {
                budgetUtilization.setBudgetName(resultSet.getString("budget_name"));
            }
            if (budgetUtilization.getBudgetLimit() == null) {
                budgetUtilization.setBudgetLimit(resultSet.getBigDecimal("budget_spend_limit"));
            }
            return BudgetCategoryUtilization.builder()
                .categoryId(resultSet.getInt("category_id"))
                .categoryName(resultSet.getString("category_name"))
                .spendLimit(resultSet.getBigDecimal("category_spend_limit"))
                .totalSpent(resultSet.getBigDecimal("total_spent"))
                .budgetUsage(resultSet.getFloat("budget_usage_percentage"))
                .isPartOfBudget(resultSet.getBoolean("is_category_part_of_budget"))
                .build();
        },
        userId, fromDateTime, toDateTime);
        budgetUtilization.setUtilizations(utilizations);
        return budgetUtilization;
    }
}
