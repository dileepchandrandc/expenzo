package com.expenzo.transactionservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.transactionservices.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
        
}
