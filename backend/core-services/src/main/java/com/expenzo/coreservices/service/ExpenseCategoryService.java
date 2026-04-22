package com.expenzo.coreservices.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public PaginatedResponse<ExpenseCategoryDto> list(Integer userId, int page) {
        Page<ExpenseCategory> bankAccounts = expenseCategoryRepository.findValidCategories(userId, PageRequest.of(page, 10));
        return new PaginatedResponse<>(bankAccounts.stream().map(this::toDto).toList(), bankAccounts.hasNext());
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
