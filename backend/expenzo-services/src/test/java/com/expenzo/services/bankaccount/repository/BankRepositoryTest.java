package com.expenzo.services.bankaccount.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.expenzo.services.bankaccount.repository.model.Bank;

@DataJpaTest
class BankRepositoryTest {

    @Autowired
    private BankRepository bankRepository;

    @Test
    void findAllByOrderByNameAsc_shouldReturnBanksSorted() {
        bankRepository.save(Bank.builder().name("Zeta Bank").shortName("ZB").build());
        bankRepository.save(Bank.builder().name("Alpha Bank").shortName("AB").build());
        bankRepository.save(Bank.builder().name("Mid Bank").shortName("MB").build());

        List<Bank> banks = bankRepository.findAllByOrderByNameAsc();

        assertThat(banks).hasSize(3);
        assertThat(banks.get(0).getName()).isEqualTo("Alpha Bank");
        assertThat(banks.get(1).getName()).isEqualTo("Mid Bank");
        assertThat(banks.get(2).getName()).isEqualTo("Zeta Bank");
    }

    @Test
    void save_shouldPersistBank_withGeneratedId() {
        Bank saved = bankRepository.save(Bank.builder().name("HDFC Bank").shortName("HDFC").build());

        assertThat(saved.getId()).isNotBlank();
        assertThat(bankRepository.findById(saved.getId())).isPresent();
    }
}
