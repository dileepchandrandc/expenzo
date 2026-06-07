package com.expenzo.services.dto.expense;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchExpenseRequest {

    private List<ExpenseDto> newExpesnes;
    private Map<Integer, ExpenseDto> updatedExpenses;
}
