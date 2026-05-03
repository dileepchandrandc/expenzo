package com.expenzo.services.service;

import org.springframework.stereotype.Service;

import com.expenzo.services.enums.PaymentChannel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentChannelService {

    private final BankAccountService bankAccountService;
    private final CreditCardService creditCardService;
    private final DebitCardService debitCardService;

    public boolean checkPaymentChannelExistis(Integer userId, PaymentChannel channelType, Integer channelId) {
        return switch (channelType) {
            case BANK_ACCOUNT -> bankAccountService.checkBankAccountExists(userId, channelId);
            case CREDIT_CARD -> creditCardService.checkCreditCardExists(userId, channelId);
            case DEBIT_CARD -> debitCardService.checkDebitCardExists(userId, channelId);
            case WALLET -> false;
        };
    }
}
