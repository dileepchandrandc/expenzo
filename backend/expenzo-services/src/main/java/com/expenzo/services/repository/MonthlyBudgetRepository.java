package com.expenzo.services.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.budget.MonthlyBudget;

@Repository
public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Integer> {

    Optional<MonthlyBudget> findByUserIdAndId(Integer userId, Integer id);

    Optional<MonthlyBudget> findByUserId(Integer userId);
}
