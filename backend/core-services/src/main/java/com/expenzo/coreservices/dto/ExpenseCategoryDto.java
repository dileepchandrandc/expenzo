package com.expenzo.coreservices.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategoryDto {

    private Integer id;
    private String name;
    private Boolean isSystemGenerated;
}
