package com.expenzo.services.dto.expense;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExpenseProcessResponse {

    private int totalRecords;
    private int duplicateRecords;
    private int newRecords;
    private List<List<ExpenseDto>> duplicateExpenses;
    private List<ExpenseDto> newExpenses;
}
