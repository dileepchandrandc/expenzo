package com.expenzo.services.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.dto.expense.BatchExpenseProcessRequest;
import com.expenzo.services.dto.expense.BatchExpenseProcessResponse;
import com.expenzo.services.dto.expense.BatchExpenseRequest;
import com.expenzo.services.dto.expense.DailySpendingTrendDto;
import com.expenzo.services.dto.expense.ExpenseBucketDto;
import com.expenzo.services.dto.expense.ExpenseCategoryGroupedResponseDto;
import com.expenzo.services.dto.expense.ExpenseDto;
import com.expenzo.services.dto.expense.MonthlyExpenseOverview;
import com.expenzo.services.dto.payment.PaymentChannelDto;
import com.expenzo.services.enums.DuplicateMatchingStrategy;
import com.expenzo.services.enums.PaymentChannel;
import com.expenzo.services.model.transaction.Transaction;
import com.expenzo.services.repository.ExpenseRepository;
import com.expenzo.services.repository.TransactionRepository;
import com.expenzo.services.utils.DateTimeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TransactionRepository transactionRepository;

    public PaginatedResponse<ExpenseDto> getExpenses(Integer userId, Integer year, Integer month, int page, int size, Integer categoryId) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        List<ExpenseDto> expenses =  expenseRepository.getExpense(userId, startEndDates[0], startEndDates[1], page * size, size + 1, categoryId);
        boolean hasNext = false;
        if (expenses.size() > size) {
            hasNext = true;
            expenses = expenses.subList(0, size);
        }
        return new PaginatedResponse<>(expenses, hasNext);
    }

    public List<ExpenseCategoryGroupedResponseDto> getExpenseGroupedByCategory(Integer userId, Integer year, Integer month) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        return expenseRepository.getExpenseGroupedByCategory(userId, startEndDates[0], startEndDates[1]);
    }

    public List<ExpenseBucketDto> getExpenseBucket(Integer userId) {
        return expenseRepository.getExpenseBuckets(userId);
    }

    public MonthlyExpenseOverview getMonthlyOverview(Integer userId, Integer year, Integer month) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        return expenseRepository.getMonthlyExpenseOverview(userId, startEndDates[0], startEndDates[1], 8);
    }

    public List<DailySpendingTrendDto> getDailySpendingTrend(Integer userId, Integer year, Integer month) {
        LocalDateTime[] startEndDates = DateTimeUtils.getBoundaryDateTimes(year, month);
        return expenseRepository.getDailySpendingTrend(userId, startEndDates[0], startEndDates[1]);
    }

    public BatchExpenseProcessResponse processExpenseFromFile(BatchExpenseProcessRequest request, MultipartFile file,
            Integer userId, PaymentChannel paymentChannelType, Integer paymentChannelId) {
        List<ExpenseDto> newExpenses = new ArrayList<>();
        List<List<ExpenseDto>> duplicateExpenses = new ArrayList<>();
        int totalRecords = 0;
        int duplicateCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Skip header row
            String header = reader.readLine();
            if (header == null) {
                return buildResponse(0, 0, 0, duplicateExpenses, newExpenses);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                totalRecords++;
                String[] columns = parseCsvLine(line);
                if (columns.length < 4) continue;

                String dateStr = columns[0].trim();
                String amountStr = columns[1].trim();
                String title = columns[2].trim();
                String description = columns.length > 3 ? columns[3].trim() : "";

                BigDecimal amount;
                LocalDateTime timestamp;
                try {
                    amount = new BigDecimal(amountStr.replace(",", ""));
                    timestamp = parseDate(dateStr);
                } catch (NumberFormatException | DateTimeParseException e) {
                    log.warn("Skipping invalid row: {}", line);
                    continue;
                }

                ExpenseDto newExpense = ExpenseDto.builder()
                        .amount(amount)
                        .spentOn(timestamp)
                        .title(title)
                        .description(description)
                        .paymentSource(PaymentChannelDto.builder()
                                .channelType(paymentChannelType)
                                .channelId(paymentChannelId)
                                .build())
                        .build();

                if (request.isRemoveDuplicates()) {
                    LocalDateTime searchStart;
                    LocalDateTime searchEnd;
                    if (request.getDuplicateMatchingStrategy() == DuplicateMatchingStrategy.SAME_DAY) {
                        searchStart = timestamp.toLocalDate().atStartOfDay();
                        searchEnd = timestamp.toLocalDate().atTime(LocalTime.MAX);
                    } else { // ONE_DAY_DIFF
                        searchStart = timestamp.toLocalDate().minusDays(1).atStartOfDay();
                        searchEnd = timestamp.toLocalDate().plusDays(1).atTime(LocalTime.MAX);
                    }

                    List<Transaction> matches = transactionRepository.findDuplicates(userId, amount, searchStart, searchEnd);
                    if (!matches.isEmpty()) {
                        duplicateCount++;
                        Transaction existing = matches.getFirst();
                        ExpenseDto existingExpense = ExpenseDto.builder()
                                .id(existing.getId())
                                .amount(existing.getAmount())
                                .spentOn(existing.getTimestamp())
                                .title(existing.getTitle())
                                .description(existing.getDescription())
                                .paymentSource(PaymentChannelDto.builder()
                                        .channelType(existing.getSourceType())
                                        .channelId(existing.getSourceId())
                                        .build())
                                .build();
                        duplicateExpenses.add(List.of(existingExpense, newExpense));
                        continue;
                    }
                }
                newExpenses.add(newExpense);
            }
        } catch (Exception e) {
            log.error("Error processing expense file", e);
            throw new RuntimeException("Failed to process expense file: " + e.getMessage());
        }

        return buildResponse(totalRecords, duplicateCount, newExpenses.size(), duplicateExpenses, newExpenses);
    }

    private BatchExpenseProcessResponse buildResponse(int totalRecords, int duplicateRecords, int newRecords,
            List<List<ExpenseDto>> duplicateExpenses, List<ExpenseDto> newExpenses) {
        return BatchExpenseProcessResponse.builder()
                .totalRecords(totalRecords)
                .duplicateRecords(duplicateRecords)
                .newRecords(newRecords)
                .duplicateExpenses(duplicateExpenses)
                .newExpenses(newExpenses)
                .build();
    }

    private LocalDateTime parseDate(String dateStr) {
        // Try multiple date formats
        String[] formats = {"d-M-yy", "d-M-yyyy", "dd-MM-yy", "dd-MM-yyyy", "M/d/yy", "M/d/yyyy"};
        for (String format : formats) {
            try {
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
                return date.atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new DateTimeParseException("Unable to parse date: " + dateStr, dateStr, 0);
    }

    private String[] parseCsvLine(String line) {
        // Simple CSV parsing that handles quoted values
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    public void applyBatchExpenseProcess(BatchExpenseRequest request, Integer userId, PaymentChannel paymentChannelType,
            Integer paymentChannelId) {
        // Add new expenses as transactions
        if (request.getNewExpesnes() != null) {
            for (ExpenseDto expense : request.getNewExpesnes()) {
                Transaction transaction = toTransaction(expense, userId);
                transactionRepository.save(transaction);
            }
        }
        // Update duplicate expenses if the user modified date, title, or description
        if (request.getUpdatedExpenses() != null) {
            for (Map.Entry<Integer, ExpenseDto> entry : request.getUpdatedExpenses().entrySet()) {
                Integer transactionId = entry.getKey();
                ExpenseDto updated = entry.getValue();
                transactionRepository.findById(transactionId).ifPresent(existing -> {
                    if (updated.getSpentOn() != null) {
                        existing.setTimestamp(updated.getSpentOn());
                    }
                    if (updated.getTitle() != null) {
                        existing.setTitle(updated.getTitle());
                    }
                    if (updated.getDescription() != null) {
                        existing.setDescription(updated.getDescription());
                    }
                    transactionRepository.save(existing);
                });
            }
        }
    }

    private Transaction toTransaction(ExpenseDto expense, Integer userId) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType(com.expenzo.services.enums.TransactionType.EXPENSE);
        transaction.setAmount(expense.getAmount());
        transaction.setTitle(expense.getTitle());
        transaction.setDescription(expense.getDescription());
        transaction.setTimestamp(expense.getSpentOn());
        if (expense.getPaymentSource() != null) {
            transaction.setSourceType(expense.getPaymentSource().getChannelType());
            transaction.setSourceId(expense.getPaymentSource().getChannelId());
        }
        return transaction;
    }
}
