package com.expenzo.services.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.dto.expense.BatchExpenseProcessRequest;
import com.expenzo.services.dto.expense.BatchExpenseProcessResponse;
import com.expenzo.services.dto.expense.BatchExpenseRequest;
import com.expenzo.services.dto.expense.DailySpendingTrendDto;
import com.expenzo.services.dto.expense.ExpenseBucketDto;
import com.expenzo.services.dto.expense.ExpenseCategoryGroupedResponseDto;
import com.expenzo.services.dto.expense.ExpenseDto;
import com.expenzo.services.dto.expense.ExpenseImportRequest;
import com.expenzo.services.dto.expense.MonthlyExpenseOverview;
import com.expenzo.services.enums.PaymentChannel;
import com.expenzo.services.service.ExpenseService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("bucket")
    public List<ExpenseBucketDto> getExpenseBucket(@RequestHeader("user-id") Integer userId) {
        return expenseService.getExpenseBucket(userId);
    }
    @GetMapping("/year/{year}/month/{month}")
    public PaginatedResponse<ExpenseDto> getExpenseByYearAndMonth(@RequestHeader("user-id") Integer userId, 
        @PathVariable("year") Integer year, @PathVariable("month") Integer month, 
        @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "category", required = false) Integer categoryId) {
        return expenseService.getExpenses(userId, year, month, page, size, categoryId);
    }

    @GetMapping("group/category/year/{year}/month/{month}")
    public List<ExpenseCategoryGroupedResponseDto> getExpenseGroupByCategory(@RequestHeader("user-id") Integer userId, 
        @PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        return expenseService.getExpenseGroupedByCategory(userId, year, month);
    }

    @GetMapping("overview/year/{year}/month/{month}")
    public MonthlyExpenseOverview getMonthlyOverview(@RequestHeader("user-id") Integer userId, 
        @PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        return expenseService.getMonthlyOverview(userId, year, month);
    }

    @GetMapping("spend-trend/daily/year/{year}/month/{month}")
    public List<DailySpendingTrendDto> getDailySpendigTrend(@RequestHeader("user-id") Integer userId, 
        @PathVariable("year") Integer year, @PathVariable("month") Integer month) {
            return expenseService.getDailySpendingTrend(userId, year, month);
    }

    @PostMapping(
        value = "upload-file/{paymentChannelType}/{paymentChannelId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BatchExpenseProcessResponse processExpenseFile(@ModelAttribute ExpenseImportRequest importRequest,
        @RequestHeader("user-id") Integer userId, @PathVariable("paymentChannelType") PaymentChannel paymentChannelType, @PathVariable("paymentChannelId") Integer paymentChannelId) {
        BatchExpenseProcessRequest request = new BatchExpenseProcessRequest();
        request.setRemoveDuplicates(importRequest.isRemoveDuplicates());
        request.setDuplicateMatchingStrategy(importRequest.getDuplicateMatchingStrategy());
        return expenseService.processExpenseFromFile(request, importRequest.getFile(), userId, paymentChannelType, paymentChannelId);
    }

    @PostMapping(
        value = "upload-file/{paymentChannelType}/{paymentChannelId}/apply"
    )
    public void applyBatchExpenseProcess(@RequestHeader("user-id") Integer userId, @PathVariable("paymentChannelType") PaymentChannel paymentChannelType, @PathVariable("paymentChannelId") Integer paymentChannelId,
        @RequestBody BatchExpenseRequest request) {
        expenseService.applyBatchExpenseProcess(request, userId, paymentChannelType, paymentChannelId);
    }
}
