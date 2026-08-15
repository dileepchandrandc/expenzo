package com.expenzo.services.bankaccount.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.bankaccount.repository.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {

    List<Bank> findAllByOrderByNameAsc();
}
