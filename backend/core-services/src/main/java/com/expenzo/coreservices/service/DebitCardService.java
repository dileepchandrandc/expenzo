package com.expenzo.coreservices.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.coreservices.dto.BankAccountDto;
import com.expenzo.coreservices.dto.BankDto;
import com.expenzo.coreservices.dto.DebitCardDto;
import com.expenzo.coreservices.dto.PaginatedResponse;
import com.expenzo.coreservices.model.payment.DebitCard;
import com.expenzo.coreservices.repository.DebitCardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DebitCardService {

    private final DebitCardRepository debitCardRepository;

    public PaginatedResponse<DebitCardDto> list(Integer userId, int page) {
        List<DebitCard> debitCards = debitCardRepository.findByUserId(userId);
        return new PaginatedResponse<>(debitCards.stream().map(this::toDto).toList(), false);
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
