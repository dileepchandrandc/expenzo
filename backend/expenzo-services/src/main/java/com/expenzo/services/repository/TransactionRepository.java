package com.expenzo.services.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.transaction.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Optional<Transaction> findByUserIdAndId(Integer userId, Integer id);

    @Query(nativeQuery = true,
        value = "select * from transaction t left join expense_category ec on ec.id = t.expense_category_id where t.type = 'EXPENSE' and t.user_id = :userId and t.timestamp >= :startDateTime and t.timestamp <= :endDateTime"
    )
    List<Transaction> getByYearAndMonth(@Param("userId") Integer userId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, Pageable page);

    @Query("select t from Transaction t where t.type = 'EXPENSE' and t.userId = :userId and t.amount = :amount and t.timestamp >= :startDate and t.timestamp <= :endDate")
    List<Transaction> findDuplicates(@Param("userId") Integer userId, @Param("amount") BigDecimal amount,
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
