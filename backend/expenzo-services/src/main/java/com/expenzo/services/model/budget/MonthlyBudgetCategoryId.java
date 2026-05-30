package com.expenzo.services.model.budget;

import java.io.Serializable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonthlyBudgetCategoryId implements Serializable {
    private Integer budget;
    private Integer category;
}
