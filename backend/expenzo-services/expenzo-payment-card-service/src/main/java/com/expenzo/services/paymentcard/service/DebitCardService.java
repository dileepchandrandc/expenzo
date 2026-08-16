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
import com.expenzo.services.paymentcard.dto.DebitCardResponse;
import com.expenzo.services.paymentcard.repository.DebitCardRepository;
import com.expenzo.services.paymentcard.repository.model.DebitCard;
import com.expenzo.services.paymentcard.request.CreateDebitCardRequest;
import com.expenzo.services.paymentcard.request.UpdateDebitCardRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public DebitCardResponse create(CreateDebitCardRequest request) {
        validateBankAccount(request.getBankAccountId());

        DebitCard debitCard = DebitCard.builder()
            .userId(getCurrentUserId())
            .bankAccountId(request.getBankAccountId())
            .cardNumber(normalizeCardNumber(request.getCardNumber()))
            .validFrom(LocalDate.now())
            .validTo(parseExpiry(request.getValidTo()))
            .nickName(request.getNickName())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build();

        validateExpiry(debitCard.getValidTo());

        debitCard = debitCardRepository.save(debitCard);
        return toDebitCardResponse(debitCard);
    }

    @Transactional
    public DebitCardResponse update(String id, UpdateDebitCardRequest request) {
        DebitCard debitCard = findOwned(id);

        if (request.getBankAccountId() != null) {
            validateBankAccount(request.getBankAccountId());
            debitCard.setBankAccountId(request.getBankAccountId());
        }
        if (request.getCardNumber() != null) {
            debitCard.setCardNumber(normalizeCardNumber(request.getCardNumber()));
        }
        if (request.getValidTo() != null) {
            debitCard.setValidTo(parseExpiry(request.getValidTo()));
        }
        if (request.getNickName() != null) {
            debitCard.setNickName(request.getNickName());
        }

        validateExpiry(debitCard.getValidTo());
        debitCard.setUpdatedAt(OffsetDateTime.now());
        debitCard = debitCardRepository.save(debitCard);
        return toDebitCardResponse(debitCard);
    }

    @Transactional(readOnly = true)
    public DebitCardResponse get(String id) {
        return toDebitCardResponse(findOwned(id));
    }

    @Transactional
    public void delete(String id) {
        DebitCard debitCard = findOwned(id);
        debitCard.setDeletedAt(OffsetDateTime.now());
        debitCard.setActive(false);
        debitCard.setUpdatedAt(OffsetDateTime.now());
        debitCardRepository.save(debitCard);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<DebitCardResponse> list(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<DebitCardResponse> debitCardPage = debitCardRepository
            .findByUserIdAndDeletedAtIsNull(getCurrentUserId(), pageable)
            .map(this::toDebitCardResponse);
        return new PaginatedResponse<>(
            debitCardPage.getContent(),
            debitCardPage.hasNext(),
            size
        );
    }

    private DebitCard findOwned(String id) {
        return debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull(id, getCurrentUserId())
            .orElseThrow(() -> new EntityNotFoundException("Debit card not found with id: " + id));
    }

    private void validateBankAccount(String bankAccountId) {
        if (bankAccountId == null || bankAccountId.isBlank()) {
            throw new IllegalArgumentException("Bank account is mandatory for debit card");
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

    private DebitCardResponse toDebitCardResponse(DebitCard debitCard) {
        DebitCardResponse response = new DebitCardResponse();
        response.setId(debitCard.getId());
        response.setUserId(debitCard.getUserId());
        response.setBankAccountId(debitCard.getBankAccountId());
        response.setCardNumber(debitCard.getCardNumber());
        response.setValidFrom(debitCard.getValidFrom());
        response.setValidTo(debitCard.getValidTo());
        response.setNickName(debitCard.getNickName());
        response.setCreatedAt(debitCard.getCreatedAt());
        response.setUpdatedAt(debitCard.getUpdatedAt());
        response.setActive(debitCard.isActive());
        return response;
    }
}
