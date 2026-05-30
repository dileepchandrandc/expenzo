package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.AddBankAccountRequest;
import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.exception.BankAccountNotFoundException;
import com.expenzo.services.model.payment.Bank;
import com.expenzo.services.model.payment.BankAccount;
import com.expenzo.services.repository.BankAccountRepository;
import com.expenzo.services.repository.BankRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankRepository bankRepository;

    public List<BankAccountDto> list(Integer userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId);
        return bankAccounts.stream().map(this::toDto).toList();
    }

    public BankAccountDto add(Integer userId, AddBankAccountRequest request) {
        Bank bank = bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new BankAccountNotFoundException("Bank not found with id: " + request.getBankId()));
        BankAccount bankAccount = BankAccount.builder()
                .userId(userId)
                .bank(bank)
                .nickName(request.getNickName())
                .build();
        bankAccount = bankAccountRepository.save(bankAccount);
        return toDto(bankAccount);
    }

    public BankAccountDto update(Integer userId, Integer id, AddBankAccountRequest request) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));
        if (!bankAccount.getUserId().equals(userId)) {
            throw new BankAccountNotFoundException("Bank account not found for user");
        }
        Bank bank = bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new BankAccountNotFoundException("Bank not found with id: " + request.getBankId()));
        bankAccount.setBank(bank);
        bankAccount.setNickName(request.getNickName());
        bankAccount = bankAccountRepository.save(bankAccount);
        return toDto(bankAccount);
    }

    public void delete(Integer userId, Integer id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));
        if (!bankAccount.getUserId().equals(userId)) {
            throw new BankAccountNotFoundException("Bank account not found for user");
        }
        bankAccountRepository.delete(bankAccount);
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
