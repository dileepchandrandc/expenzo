package com.expenzo.services.paymentcard.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expenzo.services.bankaccount.repository.BankAccountRepository;
import com.expenzo.services.common.PaginatedResponse;
import com.expenzo.services.paymentcard.dto.CreditCardResponse;
import com.expenzo.services.paymentcard.repository.CreditCardRepository;
import com.expenzo.services.paymentcard.repository.model.CreditCard;
import com.expenzo.services.paymentcard.request.CreateCreditCardRequest;
import com.expenzo.services.paymentcard.request.UpdateCreditCardRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public CreditCardResponse create(CreateCreditCardRequest request) {
        validateBankAccount(request.getBankAccountId());

        CreditCard creditCard = CreditCard.builder()
            .userId(getCurrentUserId())
            .bankAccountId(request.getBankAccountId())
            .cardNumber(normalizeCardNumber(request.getCardNumber()))
            .validFrom(LocalDate.now())
            .validTo(parseExpiry(request.getValidTo()))
            .creditLimit(request.getCreditLimit())
            .billingDate(request.getBillingDate())
            .nickName(request.getNickName())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build();

        validateExpiry(creditCard.getValidTo());

        creditCard = creditCardRepository.save(creditCard);
        return toCreditCardResponse(creditCard);
    }

    @Transactional
    public CreditCardResponse update(String id, UpdateCreditCardRequest request) {
        CreditCard creditCard = findOwned(id);

        if (request.getBankAccountId() != null) {
            validateBankAccount(request.getBankAccountId());
            creditCard.setBankAccountId(request.getBankAccountId());
        }
        if (request.getCardNumber() != null) {
            creditCard.setCardNumber(normalizeCardNumber(request.getCardNumber()));
        }
        if (request.getValidTo() != null) {
            creditCard.setValidTo(parseExpiry(request.getValidTo()));
        }
        if (request.getCreditLimit() != null) {
            creditCard.setCreditLimit(request.getCreditLimit());
        }
        if (request.getBillingDate() != null) {
            creditCard.setBillingDate(request.getBillingDate());
        }
        if (request.getNickName() != null) {
            creditCard.setNickName(request.getNickName());
        }

        validateExpiry(creditCard.getValidTo());
        creditCard.setUpdatedAt(OffsetDateTime.now());
        creditCard = creditCardRepository.save(creditCard);
        return toCreditCardResponse(creditCard);
    }

    @Transactional(readOnly = true)
    public CreditCardResponse get(String id) {
        return toCreditCardResponse(findOwned(id));
    }

    @Transactional
    public void delete(String id) {
        CreditCard creditCard = findOwned(id);
        creditCard.setDeletedAt(OffsetDateTime.now());
        creditCard.setActive(false);
        creditCard.setUpdatedAt(OffsetDateTime.now());
        creditCardRepository.save(creditCard);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<CreditCardResponse> list(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<CreditCardResponse> creditCardPage = creditCardRepository
            .findByUserIdAndDeletedAtIsNull(getCurrentUserId(), pageable)
            .map(this::toCreditCardResponse);
        return new PaginatedResponse<>(
            creditCardPage.getContent(),
            creditCardPage.hasNext(),
            size
        );
    }

    private CreditCard findOwned(String id) {
        return creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull(id, getCurrentUserId())
            .orElseThrow(() -> new EntityNotFoundException("Credit card not found with id: " + id));
    }

    private void validateBankAccount(String bankAccountId) {
        if (bankAccountId == null || bankAccountId.isBlank()) {
            return;
        }
        bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull(bankAccountId, getCurrentUserId())
            .orElseThrow(() -> new EntityNotFoundException("Bank account not found with id: " + bankAccountId));
    }

    private void validateExpiry(LocalDate validTo) {
        if (!validTo.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date must be in the future");
        }
    }

    private LocalDate parseExpiry(String validTo) {
        YearMonth yearMonth = YearMonth.parse(validTo);
        return yearMonth.atEndOfMonth();
    }

    private String normalizeCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\s+", "");
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private CreditCardResponse toCreditCardResponse(CreditCard creditCard) {
        CreditCardResponse response = new CreditCardResponse();
        response.setId(creditCard.getId());
        response.setUserId(creditCard.getUserId());
        response.setBankAccountId(creditCard.getBankAccountId());
        response.setCardNumber(creditCard.getCardNumber());
        response.setValidFrom(creditCard.getValidFrom());
        response.setValidTo(creditCard.getValidTo());
        response.setCreditLimit(creditCard.getCreditLimit());
        response.setBillingDate(creditCard.getBillingDate());
        response.setNickName(creditCard.getNickName());
        response.setCreatedAt(creditCard.getCreatedAt());
        response.setUpdatedAt(creditCard.getUpdatedAt());
        response.setActive(creditCard.isActive());
        return response;
    }
}
