package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.AddDebitCardRequest;
import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.dto.payment.DebitCardDto;
import com.expenzo.services.exception.DebitCardNotFoundException;
import com.expenzo.services.model.payment.BankAccount;
import com.expenzo.services.model.payment.DebitCard;
import com.expenzo.services.repository.BankAccountRepository;
import com.expenzo.services.repository.DebitCardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final BankAccountRepository bankAccountRepository;

    public List<DebitCardDto> list(Integer userId) {
        List<DebitCard> debitCards = debitCardRepository.findByUserId(userId);
        return debitCards.stream().map(this::toDto).toList();
    }

    public DebitCardDto add(Integer userId, AddDebitCardRequest request) {
        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new DebitCardNotFoundException("Bank account not found with id: " + request.getBankAccountId()));
        DebitCard debitCard = DebitCard.builder()
                .userId(userId)
                .bankAccount(bankAccount)
                .build();
        debitCard = debitCardRepository.save(debitCard);
        return toDto(debitCard);
    }

    public DebitCardDto update(Integer userId, Integer id, AddDebitCardRequest request) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new DebitCardNotFoundException("Debit card not found with id: " + id));
        if (!debitCard.getUserId().equals(userId)) {
            throw new DebitCardNotFoundException("Debit card not found for user");
        }
        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new DebitCardNotFoundException("Bank account not found with id: " + request.getBankAccountId()));
        debitCard.setBankAccount(bankAccount);
        debitCard = debitCardRepository.save(debitCard);
        return toDto(debitCard);
    }

    public void delete(Integer userId, Integer id) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new DebitCardNotFoundException("Debit card not found with id: " + id));
        if (!debitCard.getUserId().equals(userId)) {
            throw new DebitCardNotFoundException("Debit card not found for user");
        }
        debitCardRepository.delete(debitCard);
    }

    public boolean checkDebitCardExists(Integer useerId, Integer id) {
        return debitCardRepository.checkDebitCardExists(useerId, id) > 0;
    }

    private DebitCardDto toDto(DebitCard debitCard) {
        return DebitCardDto.builder()
            .id(debitCard.getId())
            .bankAccount(
                BankAccountDto.builder()
                    .id(debitCard.getBankAccount().getId())
                    .nickName(debitCard.getBankAccount().getNickName())
                    .bank(BankDto.builder().id(debitCard.getBankAccount().getBank().getId()).name(debitCard.getBankAccount().getBank().getName()).build())
                    .build()
            )
            .build();
    }
}
