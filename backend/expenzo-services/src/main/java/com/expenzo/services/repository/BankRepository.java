package com.expenzo.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.payment.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer> {

}
