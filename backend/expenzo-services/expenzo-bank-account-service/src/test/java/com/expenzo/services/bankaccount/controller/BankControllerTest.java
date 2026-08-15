package com.expenzo.services.bankaccount.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.bankaccount.service.BankService;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @Mock
    private BankService bankService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BankController(bankService)).build();
    }

    @Test
    void list_shouldReturnBanks() throws Exception {
        BankResponse hdfc = new BankResponse();
        hdfc.setId("bank-1");
        hdfc.setName("HDFC Bank");
        hdfc.setShortName("HDFC");

        when(bankService.list()).thenReturn(List.of(hdfc));

        mockMvc.perform(get("/bank/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("HDFC Bank"))
            .andExpect(jsonPath("$[0].shortName").value("HDFC"));
    }
}
