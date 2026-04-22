package com.expenzo.coreservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.coreservices.dto.ExpenseDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/list/year/{year}/month/{month}")
    public PaginatedResponse<ExpenseDto> list(@RequestHeader("user-id") Integer userId, @RequestParam("page") int page, @PathVariable("year") int year, @PathVariable("month") int month) {
        return expenseService.listByMonthAndYear(userId, year, month, page);
    }
}
