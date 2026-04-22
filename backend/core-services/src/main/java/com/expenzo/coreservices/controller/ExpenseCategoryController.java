package com.expenzo.coreservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.coreservices.dto.ExpenseCategoryDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.service.ExpenseCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/expense-category")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping("/list")
    public PaginatedResponse<ExpenseCategoryDto> list(@RequestHeader("user-id") Integer userId, @RequestParam("page") int page) {
        return expenseCategoryService.list(userId, page);
    }
}
