package com.expenzo.services.bankaccount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.bankaccount.repository.BankRepository;
import com.expenzo.services.bankaccount.repository.model.Bank;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    public List<BankResponse> list() {
        return bankRepository.findAllByOrderByNameAsc().stream()
            .map(this::toBankResponse)
            .toList();
    }

    private BankResponse toBankResponse(Bank bank) {
        BankResponse response = new BankResponse();
        response.setId(bank.getId());
        response.setName(bank.getName());
        response.setShortName(bank.getShortName());
        return response;
    }
}
