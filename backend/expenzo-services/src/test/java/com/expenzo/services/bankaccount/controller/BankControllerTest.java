package com.expenzo.services.bankaccount.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.expenzo.services.auth.config.JwtFilterChain;
import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.bankaccount.service.BankService;

@WebMvcTest(BankController.class)
@AutoConfigureMockMvc(addFilters = false)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankService bankService;

    @MockBean
    private JwtFilterChain jwtFilterChain;

    @Test
    @WithMockUser
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
