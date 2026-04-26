package com.expenzo.coreservices.dto;

import java.math.BigDecimal;

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
public class GroupExpenseByCategory {

    private Integer categoryId;
    private String categoryName;
    private BigDecimal amount;
}
