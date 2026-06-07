package com.expenzo.services.dto.expense;

import com.expenzo.services.enums.DuplicateMatchingStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchExpenseProcessRequest {
    private boolean removeDuplicates;
    private DuplicateMatchingStrategy duplicateMatchingStrategy;
}
