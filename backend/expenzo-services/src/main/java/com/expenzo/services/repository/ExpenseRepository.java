package com.expenzo.services.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.expenzo.services.dto.expense.ExpenseCategoryDto;
import com.expenzo.services.dto.expense.ExpenseCategoryGroupedResponseDto;
import com.expenzo.services.dto.expense.ExpenseDto;
import com.expenzo.services.enums.PaymentChannel;
import com.expenzo.services.repository.query.ExpnseQueries;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExpenseRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<ExpenseDto> getExpense(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int fromIndex, int size) {
        
        return jdbcTemplate.query(ExpnseQueries.FETCH_EXPENSES_BY_YEAR_MONTH, 
            (resultSet, rowNum) -> {
                return ExpenseDto.builder()
                    .id(resultSet.getInt("id"))
                    .amount(resultSet.getBigDecimal("amount"))
                    .title(resultSet.getString("title"))
                    .title(resultSet.getString("description"))
                    .spentOn(resultSet.getTimestamp("timestamp").toLocalDateTime())
                    .paymetSourceType(PaymentChannel.valueOf(resultSet.getString("source_type")))
                    .paymentSourceId(resultSet.getInt("source_id"))
                    .category(resultSet.getString("category_id") == null ? null : ExpenseCategoryDto.builder()
                        .id(resultSet.getInt("category_id"))
                        .name(resultSet.getString("category_name"))
                        .isSystemGenerated(resultSet.getBoolean("category_system_generated"))
                        .build()
                    )
                    .build();
            }, 
            userId, startDate, endDate, size, fromIndex);
    }

    public List<ExpenseCategoryGroupedResponseDto> getExpenseGroupedByCategory(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcTemplate.query(ExpnseQueries.FETCH_EXPENSE_GROUPED_BY_CATEGORY, 
            (resultSet, rowNum) -> {
                return ExpenseCategoryGroupedResponseDto.builder()
                    .category(ExpenseCategoryDto.builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .isSystemGenerated(resultSet.getBoolean("is_system_generated"))
                        .build()
                    )
                    .amount(resultSet.getBigDecimal("total_expense"))
                    .build();
            }, 
            userId, startDate, endDate);
    }
}
