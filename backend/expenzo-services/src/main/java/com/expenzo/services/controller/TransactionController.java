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

import com.expenzo.services.dto.transaction.AddTransactionRequest;
import com.expenzo.services.dto.transaction.TransactionDto;
import com.expenzo.services.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Void> addTransaction(@RequestHeader("user-id") Integer userId, @RequestBody AddTransactionRequest request) {
        transactionService.addTransaction(userId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTransaction(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer transactionId, @RequestBody AddTransactionRequest request) {
        transactionService.updateTransaction(userId, transactionId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer transactionId) {
        TransactionDto dto = transactionService.getTransaction(userId, transactionId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@RequestHeader("user-id") Integer userId, @PathVariable("id") Integer transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.ok().build();
    }
}
