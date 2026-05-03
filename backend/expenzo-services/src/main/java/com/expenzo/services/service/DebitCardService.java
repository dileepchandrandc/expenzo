package com.expenzo.services.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expenzo.services.dto.payment.BankAccountDto;
import com.expenzo.services.dto.payment.BankDto;
import com.expenzo.services.dto.payment.DebitCardDto;
import com.expenzo.services.dto.PaginatedResponse;
import com.expenzo.services.model.payment.DebitCard;
import com.expenzo.services.repository.DebitCardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DebitCardService {

    private final DebitCardRepository debitCardRepository;

    public PaginatedResponse<DebitCardDto> list(Integer userId, int page) {
        List<DebitCard> debitCards = debitCardRepository.findByUserId(userId);
        return new PaginatedResponse<>(debitCards.stream().map(this::toDto).toList(), false);
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
