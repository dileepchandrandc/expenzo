package com.expenzo.coreservices.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.expenzo.coreservices.dto.BankAccountDto;
import com.expenzo.coreservices.dto.BankDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.model.payment.BankAccount;
import com.expenzo.coreservices.repository.BankAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public PaginatedResponse<BankAccountDto> list(Integer userId, int page) {
        Page<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId, PageRequest.of(page, 10));
        return new PaginatedResponse<>(bankAccounts.stream().map(this::toDto).toList(), bankAccounts.hasNext());
    }

    private BankAccountDto toDto(BankAccount bankAccount) {
        return BankAccountDto.builder()
            .id(bankAccount.getId())
            .nickName(bankAccount.getNickName())
            .bank(
                BankDto.builder()
                    .id(bankAccount.getBank().getId())
                    .name(bankAccount.getBank().getName())
                    .build()
            )
            .build();
    }
}
