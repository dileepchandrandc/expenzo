package com.expenzo.services.repository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.expenzo.services.dto.expense.ExpenseBucketDto;
import com.expenzo.services.dto.expense.ExpenseCategoryDto;
import com.expenzo.services.dto.expense.ExpenseCategoryGroupedResponseDto;
import com.expenzo.services.dto.expense.ExpenseDto;
import com.expenzo.services.dto.expense.MonthlyExpenseOverview;
import com.expenzo.services.dto.payment.PaymentChannelDto;
import com.expenzo.services.enums.PaymentChannel;
import com.expenzo.services.repository.query.ExpnseQueries;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExpenseRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ExpenseDto> expenseMapper = (resultSet, rowNum) -> {
        return ExpenseDto.builder()
            .id(resultSet.getInt("id"))
            .amount(resultSet.getBigDecimal("amount"))
            .title(resultSet.getString("title"))
            .description(resultSet.getString("description"))
            .spentOn(resultSet.getTimestamp("timestamp").toLocalDateTime())
            .paymentSource(
                PaymentChannelDto.builder()
                .channelId(resultSet.getInt("source_id"))
                .channelName(null)
                .channelType(PaymentChannel.valueOf(resultSet.getString("source_type")))
                .bankName(resultSet.getString("source_channel_bank_name"))
                .bankAccountNickName(resultSet.getString("source_channel_bank_account_nick_name"))
                .build()
            )
            .category(resultSet.getString("category_id") == null ? null : ExpenseCategoryDto.builder()
                .id(resultSet.getInt("category_id"))
                .name(resultSet.getString("category_name"))
                .isSystemGenerated(resultSet.getBoolean("category_system_generated"))
                .build()
            )
            .build();
        };

    public List<ExpenseDto> getExpense(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int fromIndex, int size, Integer categoryId) {

        if (categoryId == null) {
            return jdbcTemplate.query(ExpnseQueries.FETCH_EXPENSES_BY_YEAR_MONTH, expenseMapper,
                userId, startDate, endDate, size, fromIndex);
        } else {
            return jdbcTemplate.query(ExpnseQueries.FETCH_EXPENSES_BY_YEAR_MONTH_CATEGORY, expenseMapper,
                userId, categoryId, startDate, endDate, size, fromIndex);
        }
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

    public List<ExpenseBucketDto> getExpenseBuckets(Integer userId) {
        return jdbcTemplate.query(ExpnseQueries.FETCH_EXPENSE_BUCKETS, 
            (resultSet, rowNum) -> {
                int year = resultSet.getInt("year");
                int month = resultSet.getInt("month");
                return ExpenseBucketDto.builder()
                    .year(year)
                    .month(month)
                    .name(Month.of(month).name() + " " + year)
                    .build();
            }, 
            userId);
    }

    public MonthlyExpenseOverview getMonthlyExpenseOverview(Integer userId, LocalDateTime startDate, LocalDateTime endDate, Integer day) {
        return jdbcTemplate.queryForObject(ExpnseQueries.FETCH_MONTHLY_EXPENSE_OVERVIEW, 
            (resultSet, rowNum) -> {
                return MonthlyExpenseOverview.builder()
                    .income(resultSet.getBigDecimal("total_income"))
                    .expense(resultSet.getBigDecimal("total_expense"))
                    .avgSpentPerDay(resultSet.getBigDecimal("avg_spent_per_day"))
                    .build();
            }, 
            day, userId, startDate, endDate);
    }
}
