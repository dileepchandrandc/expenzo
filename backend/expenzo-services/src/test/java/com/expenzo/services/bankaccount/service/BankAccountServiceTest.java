package com.expenzo.services.bankaccount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.expenzo.services.bankaccount.dto.BankAccountResponse;
import com.expenzo.services.bankaccount.enums.AccountType;
import com.expenzo.services.bankaccount.repository.BankAccountRepository;
import com.expenzo.services.bankaccount.repository.BankRepository;
import com.expenzo.services.bankaccount.repository.model.Bank;
import com.expenzo.services.bankaccount.repository.model.BankAccount;
import com.expenzo.services.bankaccount.request.CreateBankAccountRequest;
import com.expenzo.services.bankaccount.request.UpdateBankAccountRequest;
import com.expenzo.services.common.PaginatedResponse;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    private static final String CURRENT_USER_ID = "user-1";

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

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

    private Bank bank() {
        return Bank.builder().id("bank-1").name("HDFC Bank").shortName("HDFC").build();
    }

    private BankAccount account() {
        return BankAccount.builder()
            .id("acct-1")
            .userId(CURRENT_USER_ID)
            .bank(bank())
            .accountType(AccountType.SAVINGS)
            .accountNumber("123456")
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build();
    }

    @Test
    void create_shouldSaveWithCurrentUser() {
        CreateBankAccountRequest request = new CreateBankAccountRequest();
        request.setBankId("bank-1");
        request.setAccountType(AccountType.SAVINGS);
        request.setAccountNumber("123456");
        request.setNickName("Salary");

        when(bankRepository.findById("bank-1")).thenReturn(Optional.of(bank()));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        BankAccountResponse result = bankAccountService.create(request);

        assertThat(result.getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getAccountNumber()).isEqualTo("123456");
        assertThat(result.getBank().getName()).isEqualTo("HDFC Bank");
    }

    @Test
    void create_shouldThrow_whenBankNotFound() {
        CreateBankAccountRequest request = new CreateBankAccountRequest();
        request.setBankId("missing-bank");
        request.setAccountType(AccountType.SAVINGS);
        request.setAccountNumber("123456");

        when(bankRepository.findById("missing-bank")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankAccountService.create(request))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void get_shouldReturnOwnedAccount() {
        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(account()));

        BankAccountResponse result = bankAccountService.get("acct-1");

        assertThat(result.getId()).isEqualTo("acct-1");
        assertThat(result.getNickName()).isNull();
    }

    @Test
    void get_shouldThrow_whenNotOwned() {
        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankAccountService.get("acct-1"))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldChangeSelectedFields() {
        BankAccount existing = account();
        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(existing));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateBankAccountRequest request = new UpdateBankAccountRequest();
        request.setNickName("Updated Nick");

        BankAccountResponse result = bankAccountService.update("acct-1", request);

        assertThat(result.getNickName()).isEqualTo("Updated Nick");
        assertThat(result.getAccountNumber()).isEqualTo("123456");
    }

    @Test
    void delete_shouldSoftDelete() {
        BankAccount existing = account();
        when(bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull("acct-1", CURRENT_USER_ID))
            .thenReturn(Optional.of(existing));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        bankAccountService.delete("acct-1");

        assertThat(existing.isActive()).isFalse();
        assertThat(existing.getDeletedAt()).isNotNull();
        verify(bankAccountRepository).save(existing);
    }

    @Test
    void list_shouldReturnOwnedAccounts() {
        when(bankAccountRepository.findByUserIdAndDeletedAtIsNull(CURRENT_USER_ID, PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(List.of(account())));

        PaginatedResponse<BankAccountResponse> result = bankAccountService.list(0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getSize()).isEqualTo(20);
    }
}
