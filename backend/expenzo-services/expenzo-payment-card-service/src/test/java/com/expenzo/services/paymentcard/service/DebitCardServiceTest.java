package com.expenzo.services.paymentcard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.expenzo.services.paymentcard.dto.DebitCardResponse;
import com.expenzo.services.paymentcard.repository.DebitCardRepository;
import com.expenzo.services.paymentcard.repository.model.DebitCard;
import com.expenzo.services.paymentcard.request.CreateDebitCardRequest;
import com.expenzo.services.paymentcard.request.UpdateDebitCardRequest;
import com.expenzo.services.common.PaginatedResponse;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DebitCardServiceTest {

    private static final String CURRENT_USER_ID = "user-1";

    @Mock
    private DebitCardRepository debitCardRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private DebitCardService debitCardService;

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

    private DebitCard card() {
        return DebitCard.builder()
            .id("card-1")
            .userId(CURRENT_USER_ID)
            .bankAccountId("acct-1")
            .cardNumber("1234567890123456")
            .validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2029, 12, 31))
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build();
    }

    @Test
    void create_shouldSaveWithCurrentUser() {
        CreateDebitCardRequest request = new CreateDebitCardRequest();
        request.setBankAccountId("acct-1");
        request.setCardNumber("1234 5678 9012 3456");
        request.setValidTo("2029-12");
        request.setNickName("My Card");

        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(new BankAccount()));
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(inv -> inv.getArgument(0));

        DebitCardResponse result = debitCardService.create(request);

        assertThat(result.getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getCardNumber()).isEqualTo("1234567890123456"); // spaces removed
        assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2029, 12, 31));
        assertThat(result.getValidFrom()).isEqualTo(LocalDate.now());
    }

    @Test
    void create_shouldThrow_whenBankAccountMissing() {
        CreateDebitCardRequest request = new CreateDebitCardRequest();
        request.setCardNumber("1234567890123456");
        request.setValidTo("2029-12");

        assertThatThrownBy(() -> debitCardService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("mandatory");
    }

    @Test
    void create_shouldThrow_whenBankAccountNotOwned() {
        CreateDebitCardRequest request = new CreateDebitCardRequest();
        request.setBankAccountId("other-acct");
        request.setCardNumber("1234567890123456");
        request.setValidTo("2029-12");

        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("other-acct", CURRENT_USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> debitCardService.create(request))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_shouldThrow_whenExpiryInPast() {
        CreateDebitCardRequest request = new CreateDebitCardRequest();
        request.setBankAccountId("acct-1");
        request.setCardNumber("1234567890123456");
        request.setValidTo("2020-01");

        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(new BankAccount()));

        assertThatThrownBy(() -> debitCardService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("future");
    }

    @Test
    void get_shouldReturnOwnedCard() {
        when(debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(card()));

        DebitCardResponse result = debitCardService.get("card-1");

        assertThat(result.getId()).isEqualTo("card-1");
        assertThat(result.getBankAccountId()).isEqualTo("acct-1");
    }

    @Test
    void get_shouldThrow_whenNotOwned() {
        when(debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> debitCardService.get("card-1"))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldChangeSelectedFields() {
        DebitCard existing = card();
        when(debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(existing));
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateDebitCardRequest request = new UpdateDebitCardRequest();
        request.setNickName("Updated Card");

        DebitCardResponse result = debitCardService.update("card-1", request);

        assertThat(result.getNickName()).isEqualTo("Updated Card");
        assertThat(result.getCardNumber()).isEqualTo("1234567890123456");
    }

    @Test
    void delete_shouldSoftDelete() {
        DebitCard existing = card();
        when(debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull("card-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(existing));
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(inv -> inv.getArgument(0));

        debitCardService.delete("card-1");

        assertThat(existing.isActive()).isFalse();
        assertThat(existing.getDeletedAt()).isNotNull();
        verify(debitCardRepository).save(existing);
    }

    @Test
    void list_shouldReturnOwnedCards() {
        when(debitCardRepository.findByUserIdAndDeletedAtIsNull(CURRENT_USER_ID, PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(List.of(card())));

        PaginatedResponse<DebitCardResponse> result = debitCardService.list(0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getSize()).isEqualTo(20);
    }
}
