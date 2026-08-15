package com.expenzo.services.bankaccount.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.bankaccount.repository.model.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    @EntityGraph(attributePaths = "bank")
    Optional<BankAccount> findByIdAndUserIdAndDeletedAtIsNull(String id, String userId);

    @EntityGraph(attributePaths = "bank")
    Page<BankAccount> findByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);
}
