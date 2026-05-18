package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.model.payment.BankAccount;
import com.expenzo.services.repository.BankAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public List<BankAccountDto> list(Integer userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId);
        return bankAccounts.stream().map(this::toDto).toList();
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
