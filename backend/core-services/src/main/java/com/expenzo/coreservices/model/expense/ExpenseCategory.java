package com.expenzo.coreservices.model.expense;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expense_category")
public class ExpenseCategory {

    @Id
    private Integer id;
    private String name;
    private Boolean isSystemGenerated;
    private Integer userId;
}
