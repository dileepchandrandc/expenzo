package com.expenzo.services.bankaccount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.bankaccount.repository.BankRepository;
import com.expenzo.services.bankaccount.repository.model.Bank;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankService bankService;

    @Test
    void list_shouldMapBanksToResponses() {
        when(bankRepository.findAllByOrderByNameAsc()).thenReturn(List.of(
            Bank.builder().id("bank-1").name("HDFC Bank").shortName("HDFC").build(),
            Bank.builder().id("bank-2").name("ICICI Bank").shortName("ICICI").build()
        ));

        List<BankResponse> result = bankService.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("HDFC Bank");
        assertThat(result.get(0).getShortName()).isEqualTo("HDFC");
        assertThat(result.get(1).getId()).isEqualTo("bank-2");
    }

    @Test
    void list_shouldReturnEmpty_whenNoBanks() {
        when(bankRepository.findAllByOrderByNameAsc()).thenReturn(List.of());

        List<BankResponse> result = bankService.list();

        assertThat(result).isEmpty();
    }
}
