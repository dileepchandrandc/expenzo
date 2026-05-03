package com.expenzo.services.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.expense.ExpenseCategoryDto;
import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.service.ExpenseCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/expense-category")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping("/list")
    public PaginatedResponse<ExpenseCategoryDto> list(@RequestHeader("user-id") Integer userId) {
        return expenseCategoryService.list(userId);
    }
}
