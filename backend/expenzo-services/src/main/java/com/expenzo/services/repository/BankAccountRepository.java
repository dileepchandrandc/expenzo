package com.expenzo.services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.payment.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    List<BankAccount> findByUserId(Integer userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM bank_account ba WHERE ba.id = :id and ba.user_id = :userId")
    Integer checkBankAccountExists(@Param("userId") Integer userId, @Param("id") Integer id);
}
