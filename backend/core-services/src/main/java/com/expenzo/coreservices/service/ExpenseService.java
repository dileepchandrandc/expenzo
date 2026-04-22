package com.expenzo.coreservices.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.expenzo.coreservices.dto.ExpenseDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.model.expense.Expense;
import com.expenzo.coreservices.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public PaginatedResponse<ExpenseDto> listByMonthAndYear(Integer userId, int year, int month, int page) {
        Page<Expense> expenses = expenseRepository.findByYearAndMonth(userId, month, year, PageRequest.of(page, 25).withSort(Sort.by(Direction.DESC, "spent_on")));
        return new PaginatedResponse<>(expenses.stream().map(this::toDto).toList() ,expenses.hasNext());
    }

    private ExpenseDto toDto(Expense expense) {
        return ExpenseDto.builder()
            .id(expense.getId())
            .amount(expense.getAmount())
            .title(expense.getTitle())
            .description(expense.getDescription())
            .spentOn(expense.getSpentOn())
            .build();
    }
}
