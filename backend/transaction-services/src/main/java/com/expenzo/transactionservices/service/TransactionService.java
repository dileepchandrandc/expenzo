package com.expenzo.transactionservices.service;

import org.springframework.stereotype.Service;

import com.expenzo.transactionservices.dto.AddTransactionRequest;
import com.expenzo.transactionservices.model.Transaction;
import com.expenzo.transactionservices.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void addTransaction(Integer userId, AddTransactionRequest request) {
        Transaction transaction = toModel(request);
        transaction.setUserId(userId);
        transactionRepository.save(transaction);
    }

    private Transaction toModel(AddTransactionRequest request) {
        return Transaction.builder()
                .amount(request.getAmount())
                .title(request.getTitle())
                .description(request.getDescription())
                .timestamp(request.getTimestamp())
                .sourceId(request.getSourceId())
                .sourceType(request.getSourceType())
                .destId(request.getDestId())
                .destType(request.getDestType())
                .build();
    }
}
