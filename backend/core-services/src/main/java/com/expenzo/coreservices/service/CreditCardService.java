package com.expenzo.coreservices.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.coreservices.dto.BankAccountDto;
import com.expenzo.coreservices.dto.BankDto;
import com.expenzo.coreservices.dto.CreditCardDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.model.payment.CreditCard;
import com.expenzo.coreservices.repository.CreditCardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public PaginatedResponse<CreditCardDto> list(Integer userId, int page) {
        List<CreditCard> creditCards = creditCardRepository.findByUserId(userId);
        return new PaginatedResponse<>(creditCards.stream().map(this::toDto).toList(), false);
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
