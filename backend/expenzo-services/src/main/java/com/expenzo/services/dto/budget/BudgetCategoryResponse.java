package com.expenzo.services.dto.budget;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetCategoryResponse {
    private Integer categoryId;
    private String categoryName;
    private BigDecimal spendLimit;
}
