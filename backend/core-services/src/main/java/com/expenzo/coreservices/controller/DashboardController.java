package com.expenzo.coreservices.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.coreservices.dto.ExpenseOverviewDto;
import com.expenzo.coreservices.dto.GroupExpenseByCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/overview")
    public ExpenseOverviewDto expenseOverview() {
        return ExpenseOverviewDto.builder()
            .income(BigDecimal.valueOf(100000))
            .bill(BigDecimal.valueOf(1000))
            .expense(BigDecimal.valueOf(40000))
            .averagePerDay(BigDecimal.valueOf(100))
            .build();
    }

    @GetMapping("/group/category")
    public List<GroupExpenseByCategory> groupExpenseByCategory() {
        return List.of(
            GroupExpenseByCategory.builder()
                .categoryId(1)
                .categoryName("Food & Drinks")
                .amount(BigDecimal.valueOf(10000))
                .build(),
            GroupExpenseByCategory.builder()
                .categoryId(2)
                .categoryName("Transportation")
                .amount(BigDecimal.valueOf(5000))
                .build(),
            GroupExpenseByCategory.builder()
                .categoryId(3)
                .categoryName("Entertainment")
                .amount(BigDecimal.valueOf(2000))
                .build(),
            GroupExpenseByCategory.builder()
                .categoryId(3)
                .categoryName("Health")
                .amount(BigDecimal.valueOf(500))
                .build()
        );
    }
}
