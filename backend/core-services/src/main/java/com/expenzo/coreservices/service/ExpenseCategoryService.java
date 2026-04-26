package com.expenzo.coreservices.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.expenzo.coreservices.dto.ExpenseCategoryDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.model.expense.ExpenseCategory;
import com.expenzo.coreservices.repository.ExpenseCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public PaginatedResponse<ExpenseCategoryDto> list(Integer userId) {
        List<ExpenseCategory> bankAccounts = expenseCategoryRepository.findValidCategories(userId);
        return new PaginatedResponse<>(bankAccounts.stream().map(this::toDto).toList(), false);
    }

    public Optional<ExpenseCategoryDto> findById(Integer id, Integer userId) {
        return expenseCategoryRepository.findCategoryById(id, userId).map(this::toDto);
    }

    private ExpenseCategoryDto toDto(ExpenseCategory category) {
        return ExpenseCategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .isSystemGenerated(category.getIsSystemGenerated())
            .build();
    }
}
