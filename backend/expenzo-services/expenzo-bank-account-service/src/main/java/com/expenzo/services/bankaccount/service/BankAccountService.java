package com.expenzo.services.bankaccount.service;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenzo.services.bankaccount.dto.BankAccountResponse;
import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.common.PaginatedResponse;
import com.expenzo.services.bankaccount.repository.BankAccountRepository;
import com.expenzo.services.bankaccount.repository.BankRepository;
import com.expenzo.services.bankaccount.repository.model.Bank;
import com.expenzo.services.bankaccount.repository.model.BankAccount;
import com.expenzo.services.bankaccount.request.CreateBankAccountRequest;
import com.expenzo.services.bankaccount.request.UpdateBankAccountRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankRepository bankRepository;

    @Transactional
    public BankAccountResponse create(CreateBankAccountRequest request) {
        Bank bank = getBank(request.getBankId());

        BankAccount bankAccount = BankAccount.builder()
            .userId(getCurrentUserId())
            .bank(bank)
            .accountType(request.getAccountType())
            .accountNumber(request.getAccountNumber())
            .nickName(request.getNickName())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build();

        bankAccount = bankAccountRepository.save(bankAccount);
        return toBankAccountResponse(bankAccount);
    }

    @Transactional
    public BankAccountResponse update(String id, UpdateBankAccountRequest request) {
        BankAccount bankAccount = findOwned(id);

        if (request.getBankId() != null) {
            bankAccount.setBank(getBank(request.getBankId()));
        }
        if (request.getAccountType() != null) {
            bankAccount.setAccountType(request.getAccountType());
        }
        if (request.getAccountNumber() != null) {
            bankAccount.setAccountNumber(request.getAccountNumber());
        }
        if (request.getNickName() != null) {
            bankAccount.setNickName(request.getNickName());
        }

        bankAccount.setUpdatedAt(OffsetDateTime.now());
        bankAccount = bankAccountRepository.save(bankAccount);
        return toBankAccountResponse(bankAccount);
    }

    @Transactional(readOnly = true)
    public BankAccountResponse get(String id) {
        return toBankAccountResponse(findOwned(id));
    }

    @Transactional
    public void delete(String id) {
        BankAccount bankAccount = findOwned(id);
        bankAccount.setDeletedAt(OffsetDateTime.now());
        bankAccount.setActive(false);
        bankAccount.setUpdatedAt(OffsetDateTime.now());
        bankAccountRepository.save(bankAccount);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<BankAccountResponse> list(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<BankAccountResponse> bankAccountPage = bankAccountRepository
            .findByUserIdAndDeletedAtIsNull(getCurrentUserId(), pageable)
            .map(this::toBankAccountResponse);
        return new PaginatedResponse<>(
            bankAccountPage.getContent(),
            bankAccountPage.hasNext(),
            size
        );
    }

    private BankAccount findOwned(String id) {
        return bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull(id, getCurrentUserId())
            .orElseThrow(() -> new EntityNotFoundException("Bank account not found with id: " + id));
    }

    private Bank getBank(String bankId) {
        return bankRepository.findById(bankId)
            .orElseThrow(() -> new EntityNotFoundException("Bank not found with id: " + bankId));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private BankAccountResponse toBankAccountResponse(BankAccount bankAccount) {
        BankAccountResponse response = new BankAccountResponse();
        response.setId(bankAccount.getId());
        response.setUserId(bankAccount.getUserId());
        response.setBank(toBankResponse(bankAccount.getBank()));
        response.setAccountType(bankAccount.getAccountType());
        response.setAccountNumber(bankAccount.getAccountNumber());
        response.setNickName(bankAccount.getNickName());
        response.setCreatedAt(bankAccount.getCreatedAt());
        response.setUpdatedAt(bankAccount.getUpdatedAt());
        response.setActive(bankAccount.isActive());
        return response;
    }

    private BankResponse toBankResponse(Bank bank) {
        if (bank == null) {
            return null;
        }
        BankResponse response = new BankResponse();
        response.setId(bank.getId());
        response.setName(bank.getName());
        response.setShortName(bank.getShortName());
        return response;
    }
}
