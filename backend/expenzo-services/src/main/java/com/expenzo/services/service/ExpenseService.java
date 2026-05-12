package com.expenzo.services.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.dto.expense.ExpenseBucketDto;
import com.expenzo.services.dto.expense.ExpenseCategoryGroupedResponseDto;
import com.expenzo.services.dto.expense.ExpenseDto;
import com.expenzo.services.dto.expense.MonthlyExpenseOverview;
import com.expenzo.services.repository.ExpenseRepository;
import com.expenzo.services.utils.DateTimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public PaginatedResponse<ExpenseDto> getExpenses(Integer userId, Integer year, Integer month, int page, int size, Integer categoryId) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        List<ExpenseDto> expenses =  expenseRepository.getExpense(userId, startEndDates[0], startEndDates[1], page * size, size + 1, categoryId);
        boolean hasNext = false;
        if (expenses.size() > size) {
            hasNext = true;
            expenses = expenses.subList(0, size);
        }
        return new PaginatedResponse<>(expenses, hasNext);
    }

    public List<ExpenseCategoryGroupedResponseDto> getExpenseGroupedByCategory(Integer userId, Integer year, Integer month) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        return expenseRepository.getExpenseGroupedByCategory(userId, startEndDates[0], startEndDates[1]);
    }

    public List<ExpenseBucketDto> getExpenseBucket(Integer userId) {
        return expenseRepository.getExpenseBuckets(userId);
    }

    public MonthlyExpenseOverview getMonthlyOverview(Integer userId, Integer year, Integer month) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        return expenseRepository.getMonthlyExpenseOverview(userId, startEndDates[0], startEndDates[1], 8);
    }
}
