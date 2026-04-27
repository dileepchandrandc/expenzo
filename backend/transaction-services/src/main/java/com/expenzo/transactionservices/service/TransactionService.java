package com.expenzo.transactionservices.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.expenzo.transactionservices.dto.AddTransactionRequest;
import com.expenzo.transactionservices.enums.EventAction;
import com.expenzo.transactionservices.event.TransactionEvent;
import com.expenzo.transactionservices.exception.InvalidTransactioException;
import com.expenzo.transactionservices.exception.TransactionNotFoundException;
import com.expenzo.transactionservices.model.Transaction;
import com.expenzo.transactionservices.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaProducer kafkaProducer;
    @Value("${expenzo.event.kafka-topic.raw-transaction}")
    private String topicName;

    public void addTransaction(Integer userId, AddTransactionRequest request) {
        Transaction transaction = toModel(request);
        transaction.setUserId(userId);
        transactionRepository.save(transaction);
        TransactionEvent event = createEvent(EventAction.CREATE, transaction);
        event.setMetaData(request.getMetaData());
        sendTransactionDetails(event);
    }

    public void updateTransaction(Integer userId, Integer id, AddTransactionRequest request) {
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, id)
                                    .orElseThrow(() -> new TransactionNotFoundException("Invalid transaction detaisl"));
        updateTransaction(transaction, request);
        if (transaction == null) throw new InvalidTransactioException("Invalid transaction");
        transactionRepository.save(transaction);
        TransactionEvent event = createEvent(EventAction.UPDATE, transaction);
        sendTransactionDetails(event);
    }

    private TransactionEvent createEvent(EventAction action, Transaction transaction) {
        if (transaction == null || action == null) {
            return null;
        }
        return TransactionEvent.builder()
                .action(action)
                .amount(transaction.getAmount())
                .title(transaction.getTitle())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .sourceId(transaction.getSourceId())
                .sourceType(transaction.getSourceType())
                .destId(transaction.getDestId())
                .destType(transaction.getDestType())
                .build();
    }

    private void sendTransactionDetails(TransactionEvent event) {
        if (event == null || event.getId() == null) {
            log.error("Invalid transaction event");
            return;
        }
        if (null != topicName) {
            kafkaProducer.send(topicName, event); //Sending transactions to the kafka queue for processing and adding them to the db
            log.debug("Transaction details of {} has been sent to the topic {}", event.getId(), topicName);
        } else {
            log.warn("Could not sent the transaction: Missing kafka topic");
        }
    }

    private void updateTransaction(Transaction transaction, AddTransactionRequest request) {
        if (request == null || transaction == null) return;
        transaction.setAmount(request.getAmount());
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
