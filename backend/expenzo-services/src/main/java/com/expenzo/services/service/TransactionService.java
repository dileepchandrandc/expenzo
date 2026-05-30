package com.expenzo.services.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.transaction.AddTransactionRequest;
import com.expenzo.services.dto.transaction.TransactionDto;
import com.expenzo.services.enums.TransactionType;
import com.expenzo.services.exception.InvalidExpenseCategoryException;
import com.expenzo.services.exception.InvalidTransactioException;
import com.expenzo.services.exception.TransactionNotFoundException;
import com.expenzo.services.exception.TransactionValidationFailedException;
import com.expenzo.services.model.expense.ExpenseCategory;
import com.expenzo.services.model.transaction.Transaction;
import com.expenzo.services.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentChannelService paymentChannelService;
    private final ExpenseCategoryService expenseCategoryService;

    public void addTransaction(Integer userId, AddTransactionRequest request) {
        validateTransactionRequest(userId, request);
        Transaction transaction = toModel(request);
        transaction.setUserId(userId);
        if (request.getMetaData() != null && request.getMetaData().getExpenseCategoryId() != null) {
            Optional<ExpenseCategory> category = expenseCategoryService.getExpenseCategoryById(request.getMetaData().getExpenseCategoryId(), userId);
            if (category.isPresent()) {
                transaction.setExpenseCategory(category.get());
            } else {
                throw new InvalidExpenseCategoryException("Invalid exception category");
            }
        }
        transactionRepository.save(transaction);
    }

    public void updateTransaction(Integer userId, Integer id, AddTransactionRequest request) {
        validateTransactionRequest(userId, request);
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, id)
                                    .orElseThrow(() -> new TransactionNotFoundException("Invalid transaction detaisl"));
        applyChanges(transaction, request);
        if (transaction == null) throw new InvalidTransactioException("Invalid transaction");
        transactionRepository.save(transaction);
    }

    public TransactionDto getTransaction(Integer userId, Integer id) {
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
        return toTransactionDto(transaction);
    }

    public void deleteTransaction(Integer userId, Integer id) {
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }

    private void validateTransactionRequest(Integer userId, AddTransactionRequest request) {
        //1. Validate amount (should be greater than 0)
        if (request.getAmount() == null || request.getAmount().compareTo(new BigDecimal(0)) < 1) {
            throw new TransactionValidationFailedException("Invalid transaction amount");
        }
        //2. Validate date (should not be null)
        if (request.getTimestamp() == null) {
            throw new TransactionValidationFailedException("Transaction date is missing");
        }
        //3. Validate the transaction type (should not be null)
        if (request.getType() == null) {
            throw new TransactionValidationFailedException("Invalid transaction type");
        }
        //4. Validate payment source and destination (both should be owned by the user and should be in sync with type of the transaction)
        boolean isSourceValid = request.getSourceType() != null && paymentChannelService.checkPaymentChannelExistis(userId, request.getSourceType(), request.getSourceId());
        boolean isDestValid = request.getDestType() != null && paymentChannelService.checkPaymentChannelExistis(userId, request.getDestType(), request.getDestId());
        if (request.getType() == TransactionType.EXPENSE && !isSourceValid) {
            throw new TransactionValidationFailedException("Invalid transaction source details");
        } else if (request.getType() == TransactionType.INCOME && !isDestValid) {
            throw new TransactionValidationFailedException("Invalid transaction destination details");
        } else if (request.getType() == TransactionType.SELF_TRANSFER && (!isDestValid || !isDestValid)) {
            throw new TransactionValidationFailedException("Invalid transaction source/destination details");
        }
    }

    private void applyChanges(Transaction transaction, AddTransactionRequest request) {
        if (request == null || transaction == null) return;
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTitle(request.getTitle());
        transaction.setDescription(request.getDescription());
        transaction.setTimestamp(request.getTimestamp());
        transaction.setSourceId(request.getSourceId());
        transaction.setSourceType(request.getSourceType());
        transaction.setDestId(request.getDestId());
        transaction.setDestType(request.getDestType());
    }

    private Transaction toModel(AddTransactionRequest request) {
        return Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .title(request.getTitle())
                .description(request.getDescription())
                .timestamp(request.getTimestamp())
                .sourceId(request.getSourceId())
                .sourceType(request.getSourceType())
                .destId(request.getDestId())
                .destType(request.getDestType())
                .build();
    }

    private TransactionDto toTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .sourceType(transaction.getSourceType())
                .sourceId(transaction.getSourceId())
                .destType(transaction.getDestType())
                .destId(transaction.getDestId())
                .build();
    }
}
