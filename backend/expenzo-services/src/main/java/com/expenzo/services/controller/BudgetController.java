package com.expenzo.services.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.budget.BudgetUtilization;
import com.expenzo.services.service.BudgetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("summary/year/{year}/month/{month}")
    public BudgetUtilization getBudgetSummary(@RequestHeader("user-id") Integer userId, 
        @PathVariable("year") Integer year, @PathVariable("month") Integer month) {
            return budgetService.getBudgetSummary(userId, year, month);
    }
}
