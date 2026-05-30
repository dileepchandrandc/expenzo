package com.expenzo.services.dto.budget;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryLimit {
    private Integer categoryId;
    private BigDecimal spendLimit;
}
