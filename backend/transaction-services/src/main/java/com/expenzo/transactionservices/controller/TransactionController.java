package com.expenzo.transactionservices.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expenzo.transactionservices.dto.AddTransactionRequest;
import com.expenzo.transactionservices.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public void addTransaction(@RequestHeader("user-id") Integer userId, @RequestBody AddTransactionRequest request) {
        transactionService.addTransaction(userId, request);
    }
}
