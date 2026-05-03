package com.expenzo.services.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.model.payment.BankAccount;
import com.expenzo.services.repository.BankAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public PaginatedResponse<BankAccountDto> list(Integer userId, int page) {
        Page<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId, PageRequest.of(page, 10));
        return new PaginatedResponse<>(bankAccounts.stream().map(this::toDto).toList(), bankAccounts.hasNext());
    }

    public boolean checkBankAccountExists(Integer useerId, Integer id) {
        return bankAccountRepository.checkBankAccountExists(useerId, id) > 0;
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
