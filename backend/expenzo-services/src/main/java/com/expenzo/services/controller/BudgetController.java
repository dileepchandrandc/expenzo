package com.expenzo.services.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.services.dto.budget.BudgetResponse;
import com.expenzo.services.dto.budget.BudgetUtilization;
import com.expenzo.services.dto.budget.CreateBudgetRequest;
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

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@RequestHeader("user-id") Integer userId, @RequestBody CreateBudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<BudgetResponse> getMyBudget(@RequestHeader("user-id") Integer userId) {
        try {
            BudgetResponse response = budgetService.getMyBudget(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/my")
    public ResponseEntity<BudgetResponse> updateMyBudget(@RequestHeader("user-id") Integer userId, @RequestBody CreateBudgetRequest request) {
        BudgetResponse response = budgetService.updateMyBudget(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> deleteMyBudget(@RequestHeader("user-id") Integer userId) {
        budgetService.deleteMyBudget(userId);
        return ResponseEntity.ok().build();
    }
}
