package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.dto.payment.CreditCardDto;
import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.model.payment.CreditCard;
import com.expenzo.services.repository.CreditCardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public PaginatedResponse<CreditCardDto> list(Integer userId, int page) {
        List<CreditCard> creditCards = creditCardRepository.findByUserId(userId);
        return new PaginatedResponse<>(creditCards.stream().map(this::toDto).toList(), false);
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
            .limit(creditCard.getLimit())
            .currentOutStanding(creditCard.getCurrentOutStanding())
            .build();
    }
}
