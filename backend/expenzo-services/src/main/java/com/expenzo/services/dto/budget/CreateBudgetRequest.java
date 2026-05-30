package com.expenzo.services.dto.budget;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetRequest {
    private String name;
    private BigDecimal spendLimit;
    private List<CategoryLimit> categoryLimits;
}
