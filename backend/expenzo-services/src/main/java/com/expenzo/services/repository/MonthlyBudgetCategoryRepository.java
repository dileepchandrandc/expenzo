package com.expenzo.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.budget.MonthlyBudgetCategory;

@Repository
public interface MonthlyBudgetCategoryRepository extends JpaRepository<MonthlyBudgetCategory, Integer> {
}
