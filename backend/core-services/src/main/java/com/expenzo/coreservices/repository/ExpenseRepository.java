package com.expenzo.coreservices.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expenzo.coreservices.model.expense.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    @Query(value =  "select  * from expense e where e.user_id = :userId and extract(month from e.spent_on ) = :month  and extract(year from e.spent_on ) = :year", nativeQuery = true)
    Page<Expense> findByYearAndMonth(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year, Pageable pageable);
}
