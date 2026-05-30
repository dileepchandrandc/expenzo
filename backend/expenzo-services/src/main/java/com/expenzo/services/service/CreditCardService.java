package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.AddCreditCardRequest;
import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.dto.payment.CreditCardDto;
import com.expenzo.services.exception.CreditCardNotFoundException;
import com.expenzo.services.model.payment.BankAccount;
import com.expenzo.services.model.payment.CreditCard;
import com.expenzo.services.repository.BankAccountRepository;
import com.expenzo.services.repository.CreditCardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;

    public List<CreditCardDto> list(Integer userId) {
        List<CreditCard> creditCards = creditCardRepository.findByUserId(userId);
        return creditCards.stream().map(this::toDto).toList();
    }

    public CreditCardDto add(Integer userId, AddCreditCardRequest request) {
        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new CreditCardNotFoundException("Bank account not found with id: " + request.getBankAccountId()));
        CreditCard creditCard = CreditCard.builder()
                .userId(userId)
                .bankAccount(bankAccount)
                .creditLimit(request.getLimit())
                .currentOutStanding(request.getCurrentOutStanding())
                .billingDate(request.getBillingDate())
                .build();
        creditCard = creditCardRepository.save(creditCard);
        return toDto(creditCard);
    }

    public CreditCardDto update(Integer userId, Integer id, AddCreditCardRequest request) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found with id: " + id));
        if (!creditCard.getUserId().equals(userId)) {
            throw new CreditCardNotFoundException("Credit card not found for user");
        }
        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new CreditCardNotFoundException("Bank account not found with id: " + request.getBankAccountId()));
        creditCard.setBankAccount(bankAccount);
        creditCard.setCreditLimit(request.getLimit());
        creditCard.setCurrentOutStanding(request.getCurrentOutStanding());
        creditCard.setBillingDate(request.getBillingDate());
        creditCard = creditCardRepository.save(creditCard);
        return toDto(creditCard);
    }

    public void delete(Integer userId, Integer id) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found with id: " + id));
        if (!creditCard.getUserId().equals(userId)) {
            throw new CreditCardNotFoundException("Credit card not found for user");
        }
        creditCardRepository.delete(creditCard);
    }

    public boolean checkCreditCardExists(Integer useerId, Integer id) {
        return creditCardRepository.checkCreditCardExists(useerId, id) > 0;
    }

    private CreditCardDto toDto(CreditCard creditCard) {
        return CreditCardDto.builder()
            .id(creditCard.getId())
            .bankAccount(
                BankAccountDto.builder()
                    .id(creditCard.getBankAccount().getId())
                    .nickName(creditCard.getBankAccount().getNickName())
                    .bank(BankDto.builder().id(creditCard.getBankAccount().getBank().getId()).name(creditCard.getBankAccount().getBank().getName()).build())
                    .build()
            )
            .billingDate(creditCard.getBillingDate())
            .limit(creditCard.getCreditLimit())
            .currentOutStanding(creditCard.getCurrentOutStanding())
            .build();
    }
}
