package com.expenzo.services.paymentcard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.expenzo.services.bankaccount.repository.BankAccountRepository;
import com.expenzo.services.bankaccount.repository.model.BankAccount;
import com.expenzo.services.paymentcard.dto.CreditCardResponse;
import com.expenzo.services.paymentcard.repository.CreditCardRepository;
import com.expenzo.services.paymentcard.repository.model.CreditCard;
import com.expenzo.services.paymentcard.request.CreateCreditCardRequest;
import com.expenzo.services.paymentcard.request.UpdateCreditCardRequest;
import com.expenzo.services.common.PaginatedResponse;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {

    private static final String CURRENT_USER_ID = "user-1";

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private CreditCardService creditCardService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(CURRENT_USER_ID, null)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private CreditCard card() {
        return CreditCard.builder()
            .id("card-1")
            .userId(CURRENT_USER_ID)
            .bankAccountId("acct-1")
            .cardNumber("1234567890123456")
            .validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2029, 12, 31))
            .creditLimit(new BigDecimal("50000.00"))
            .billingDate(5)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build();
    }

    @Test
    void create_shouldSaveWithCurrentUser() {
        CreateCreditCardRequest request = new CreateCreditCardRequest();
        request.setBankAccountId("acct-1");
        request.setCardNumber("1234 5678 9012 3456");
        request.setValidTo("2029-12");
        request.setCreditLimit(new BigDecimal("50000.00"));
        request.setBillingDate(5);
        request.setNickName("My Card");

        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(new BankAccount()));
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(inv -> inv.getArgument(0));

        CreditCardResponse result = creditCardService.create(request);

        assertThat(result.getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getCardNumber()).isEqualTo("1234567890123456"); // spaces removed
        assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2029, 12, 31));
        assertThat(result.getValidFrom()).isEqualTo(LocalDate.now());
    }

    @Test
    void create_shouldThrow_whenBankAccountNotOwned() {
        CreateCreditCardRequest request = new CreateCreditCardRequest();
        request.setBankAccountId("other-acct");
        request.setCardNumber("1234567890123456");
        request.setValidTo("2029-12");
        request.setCreditLimit(new BigDecimal("50000.00"));
        request.setBillingDate(5);

        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("other-acct", CURRENT_USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditCardService.create(request))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenExpiryInPast() {
        CreateCreditCardRequest request = new CreateCreditCardRequest();
        request.setCardNumber("1234567890123456");
        request.setValidTo("2020-01");
        request.setCreditLimit(new BigDecimal("50000.00"));
        request.setBillingDate(5);

        assertThatThrownBy(() -> creditCardService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("future");
    }

    @Test
    void get_shouldReturnOwnedCard() {
        when(creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(card()));

        CreditCardResponse result = creditCardService.get("card-1");

        assertThat(result.getId()).isEqualTo("card-1");
        assertThat(result.getCreditLimit()).isEqualByComparingTo("50000.00");
    }

    @Test
    void get_shouldThrow_whenNotOwned() {
        when(creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditCardService.get("card-1"))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldChangeSelectedFields() {
        CreditCard existing = card();
        when(creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(existing));
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCreditCardRequest request = new UpdateCreditCardRequest();
        request.setNickName("Updated Card");
        request.setCreditLimit(new BigDecimal("100000.00"));

        CreditCardResponse result = creditCardService.update("card-1", request);

        assertThat(result.getNickName()).isEqualTo("Updated Card");
        assertThat(result.getCreditLimit()).isEqualByComparingTo("100000.00");
        assertThat(result.getCardNumber()).isEqualTo("1234567890123456");
    }

    @Test
    void delete_shouldSoftDelete() {
        CreditCard existing = card();
        when(creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(existing));
        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(inv -> inv.getArgument(0));

        creditCardService.delete("card-1");

        assertThat(existing.isActive()).isFalse();
        assertThat(existing.getDeletedAt()).isNotNull();
        verify(creditCardRepository).save(existing);
    }

    @Test
    void list_shouldReturnOwnedCards() {
        when(creditCardRepository.findByUserIdAndDeletedAtIsNull(CURRENT_USER_ID, PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(List.of(card())));

        PaginatedResponse<CreditCardResponse> result = creditCardService.list(0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getSize()).isEqualTo(20);
    }
}
