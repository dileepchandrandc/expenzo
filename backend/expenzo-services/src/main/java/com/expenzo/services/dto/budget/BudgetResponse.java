package com.expenzo.services.dto.budget;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {
    private Integer id;
    private String name;
    private BigDecimal spendLimit;
    private List<BudgetCategoryResponse> categories;
}
