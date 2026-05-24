package com.expenzo.services.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.budget.BudgetCategoryUtilization;
import com.expenzo.services.dto.budget.BudgetUtilization;
import com.expenzo.services.repository.BudgetRepository;
import com.expenzo.services.utils.DateTimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetUtilization getBudgetSummary(Integer userId, Integer year, Integer month) {
        LocalDateTime[] dates = DateTimeUtils.getBoundaryDateTimes(year, month);
        BudgetUtilization report = budgetRepository.getBudgetSummaryReport(userId, dates[0], dates[1]);
        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal untrackedSpent = BigDecimal.ZERO;
        BigDecimal uncategorizedSpent = BigDecimal.ZERO;
        for (BudgetCategoryUtilization cu: report.getUtilizations()) {
            totalSpent = totalSpent.add(cu.getTotalSpent());
            if (!cu.isPartOfBudget()) {
                if (cu.getCategoryId() != 0) {
                    untrackedSpent = untrackedSpent.add(cu.getTotalSpent());
                } else {
                    uncategorizedSpent = uncategorizedSpent.add(cu.getTotalSpent());
                }
            }
        }
        report.setTotalSpent(totalSpent);
        report.setUntrackedSpent(untrackedSpent);
        report.setUncategorizedSpent(uncategorizedSpent);
        report.setBudgetUsage(totalSpent.divide(report.getBudgetLimit()).multiply(BigDecimal.valueOf(100)).floatValue());
        report.setUntrackedUsage(untrackedSpent.divide(report.getBudgetLimit()).multiply(BigDecimal.valueOf(100)).floatValue());
        report.setUncategorizedUsage(uncategorizedSpent.divide(report.getBudgetLimit()).multiply(BigDecimal.valueOf(100)).floatValue());
        return report;
    }
}
