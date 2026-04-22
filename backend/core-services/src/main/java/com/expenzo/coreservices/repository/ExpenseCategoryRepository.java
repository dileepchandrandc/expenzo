package com.expenzo.coreservices.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expenzo.coreservices.model.expense.ExpenseCategory;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {

    @Query(value = "select ec from ExpenseCategory ec where ec.isSystemGenerated = true or ec.userId = :userId")
    Page<ExpenseCategory> findValidCategories(@Param("userId")Integer userId, Pageable pageable);

    @Query(value = "select ec from ExpenseCategory ec where ec.id = :id and (ec.isSystemGenerated = true or ec.userId = :userId)")
    Optional<ExpenseCategory> findCategoryById(@Param("id") Integer id, @Param("userId") Integer userId);
}
