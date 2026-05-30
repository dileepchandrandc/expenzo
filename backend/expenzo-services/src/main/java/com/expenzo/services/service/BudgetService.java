package com.expenzo.services.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenzo.services.dto.budget.BudgetCategoryResponse;
import com.expenzo.services.dto.budget.BudgetCategoryUtilization;
import com.expenzo.services.dto.budget.BudgetResponse;
import com.expenzo.services.dto.budget.BudgetUtilization;
import com.expenzo.services.dto.budget.CategoryLimit;
import com.expenzo.services.dto.budget.CreateBudgetRequest;
import com.expenzo.services.exception.BudgetNotFoundException;
import com.expenzo.services.exception.InvalidBudgetException;
import com.expenzo.services.model.budget.MonthlyBudget;
import com.expenzo.services.model.budget.MonthlyBudgetCategory;
import com.expenzo.services.model.expense.ExpenseCategory;
import com.expenzo.services.repository.BudgetRepository;
import com.expenzo.services.repository.ExpenseCategoryRepository;
import com.expenzo.services.repository.MonthlyBudgetRepository;
import com.expenzo.services.utils.DateTimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

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

    @Transactional
    public BudgetResponse createBudget(Integer userId, CreateBudgetRequest request) {
        // Ensure only one budget per user
        monthlyBudgetRepository.findByUserId(userId).ifPresent(b -> {
            throw new BudgetNotFoundException("User already has a budget");
        });
        validateCategoryLimits(request);
        MonthlyBudget budget = MonthlyBudget.builder()
                .userId(userId)
                .name(request.getName())
                .spendLimit(request.getSpendLimit())
                .categoryLimits(new ArrayList<>())
                .build();
        if (request.getCategoryLimits() != null) {
            for (CategoryLimit cl : request.getCategoryLimits()) {
                ExpenseCategory category = expenseCategoryRepository.findById(cl.getCategoryId()).orElse(null);
                budget.getCategoryLimits().add(MonthlyBudgetCategory.builder()
                        .budget(budget)
                        .category(category)
                        .userId(userId)
                        .spendLimit(cl.getSpendLimit())
                        .build());
            }
        }
        budget = monthlyBudgetRepository.save(budget);
        return toBudgetResponse(budget);
    }

    @Transactional
    public BudgetResponse updateMyBudget(Integer userId, CreateBudgetRequest request) {
        validateCategoryLimits(request);
        MonthlyBudget budget = monthlyBudgetRepository.findByUserId(userId)
                .orElseThrow(() -> new BudgetNotFoundException("No budget found for user"));
        budget.setName(request.getName());
        budget.setSpendLimit(request.getSpendLimit());
        // Replace category limits via cascade
        budget.getCategoryLimits().clear();
        if (request.getCategoryLimits() != null) {
            for (CategoryLimit cl : request.getCategoryLimits()) {
                ExpenseCategory category = expenseCategoryRepository.findById(cl.getCategoryId()).orElse(null);
                budget.getCategoryLimits().add(MonthlyBudgetCategory.builder()
                        .budget(budget)
                        .category(category)
                        .userId(userId)
                        .spendLimit(cl.getSpendLimit())
                        .build());
            }
        }
        budget = monthlyBudgetRepository.save(budget);
        return toBudgetResponse(budget);
    }

    @Transactional
    public void deleteMyBudget(Integer userId) {
        MonthlyBudget budget = monthlyBudgetRepository.findByUserId(userId)
                .orElseThrow(() -> new BudgetNotFoundException("No budget found for user"));
        monthlyBudgetRepository.delete(budget); // Cascade deletes category limits
    }

    public BudgetResponse getMyBudget(Integer userId) {
        MonthlyBudget budget = monthlyBudgetRepository.findByUserId(userId)
                .orElseThrow(() -> new BudgetNotFoundException("No budget found for user"));
        return toBudgetResponse(budget);
    }

    public boolean hasBudget(Integer userId) {
        return monthlyBudgetRepository.findByUserId(userId).isPresent();
    }

    private void validateCategoryLimits(CreateBudgetRequest request) {
        if (request.getCategoryLimits() == null || request.getCategoryLimits().isEmpty()) {
            return;
        }
        BigDecimal categoryTotal = request.getCategoryLimits().stream()
                .map(CategoryLimit::getSpendLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (categoryTotal.compareTo(request.getSpendLimit()) > 0) {
            throw new InvalidBudgetException(
                "Sum of category spend limits (₹" + categoryTotal + 
                ") exceeds the budget spend limit (₹" + request.getSpendLimit() + ")"
            );
        }
    }

    private BudgetResponse toBudgetResponse(MonthlyBudget budget) {
        List<BudgetCategoryResponse> categories = new ArrayList<>();
        if (budget.getCategoryLimits() != null) {
            for (MonthlyBudgetCategory mbc : budget.getCategoryLimits()) {
                Integer catId = mbc.getCategory() != null ? mbc.getCategory().getId() : 0;
                String catName = mbc.getCategory() != null ? mbc.getCategory().getName() : "";
                categories.add(BudgetCategoryResponse.builder()
                        .categoryId(catId)
                        .categoryName(catName)
                        .spendLimit(mbc.getSpendLimit())
                        .build());
            }
        }
        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .spendLimit(budget.getSpendLimit())
                .categories(categories)
                .build();
    }
}
