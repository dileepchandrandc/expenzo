package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.model.payment.Bank;
import com.expenzo.services.repository.BankRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    public List<BankDto> listAll() {
        return bankRepository.findAll().stream().map(this::toDto).toList();
    }

    private BankDto toDto(Bank model) {
        return BankDto.builder()
            .id(model.getId())
            .name(model.getName())
            .build();
    }
}
