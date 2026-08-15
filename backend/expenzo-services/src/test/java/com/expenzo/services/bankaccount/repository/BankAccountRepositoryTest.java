package com.expenzo.services.bankaccount.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.expenzo.services.bankaccount.enums.AccountType;
import com.expenzo.services.bankaccount.repository.model.Bank;
import com.expenzo.services.bankaccount.repository.model.BankAccount;

@DataJpaTest
class BankAccountRepositoryTest {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankRepository bankRepository;

    private static final String USER_ID = "user-123";

    private Bank saveBank() {
        return bankRepository.save(Bank.builder().name("HDFC Bank").shortName("HDFC").build());
    }

    private BankAccount saveAccount(Bank bank, String accountNumber) {
        return bankAccountRepository.save(BankAccount.builder()
            .userId(USER_ID)
            .bank(bank)
            .accountType(AccountType.SAVINGS)
            .accountNumber(accountNumber)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build());
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldReturnAccount() {
        Bank bank = saveBank();
        BankAccount account = saveAccount(bank, "123456");

        Optional<BankAccount> found =
            bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull(account.getId(), USER_ID);

        assertThat(found).isPresent();
        assertThat(found.get().getAccountNumber()).isEqualTo("123456");
        assertThat(found.get().getBank().getName()).isEqualTo("HDFC Bank");
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreOtherUser() {
        Bank bank = saveBank();
        BankAccount account = saveAccount(bank, "111222");

        Optional<BankAccount> found =
            bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull(account.getId(), "other-user");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreSoftDeleted() {
        Bank bank = saveBank();
        BankAccount account = saveAccount(bank, "333444");
        account.setDeletedAt(OffsetDateTime.now());
        account.setActive(false);
        bankAccountRepository.save(account);

        Optional<BankAccount> found =
            bankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull(account.getId(), USER_ID);

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUserIdAndDeletedAtIsNull_shouldReturnOnlyUsersAccounts() {
        Bank bank = saveBank();
        saveAccount(bank, "555666");
        saveAccount(bank, "777888");

        Page<BankAccount> page =
            bankAccountRepository.findByUserIdAndDeletedAtIsNull(USER_ID, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }
}
