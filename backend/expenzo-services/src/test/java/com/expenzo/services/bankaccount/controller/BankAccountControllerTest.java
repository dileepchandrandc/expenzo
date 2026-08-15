package com.expenzo.services.bankaccount.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.expenzo.services.auth.config.JwtFilterChain;
import com.expenzo.services.bankaccount.dto.BankAccountResponse;
import com.expenzo.services.bankaccount.dto.BankResponse;
import com.expenzo.services.bankaccount.enums.AccountType;
import com.expenzo.services.bankaccount.request.CreateBankAccountRequest;
import com.expenzo.services.bankaccount.request.UpdateBankAccountRequest;
import com.expenzo.services.bankaccount.service.BankAccountService;
import com.expenzo.services.common.PaginatedResponse;

@WebMvcTest(BankAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private JwtFilterChain jwtFilterChain;

    private BankAccountResponse accountResponse() {
        BankAccountResponse response = new BankAccountResponse();
        response.setId("acct-1");
        response.setUserId("user-1");
        response.setAccountType(AccountType.SAVINGS);
        response.setAccountNumber("123456");
        response.setCreatedAt(OffsetDateTime.now());
        response.setUpdatedAt(OffsetDateTime.now());
        response.setActive(true);
        BankResponse bank = new BankResponse();
        bank.setId("bank-1");
        bank.setName("HDFC Bank");
        bank.setShortName("HDFC");
        response.setBank(bank);
        return response;
    }

    @Test
    @WithMockUser
    void create_shouldReturn201() throws Exception {
        when(bankAccountService.create(any(CreateBankAccountRequest.class))).thenReturn(accountResponse());

        mockMvc.perform(post("/bank-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"bankId\": \"bank-1\", \"accountType\": \"SAVINGS\", \"accountNumber\": \"123456\" }"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("acct-1"))
            .andExpect(jsonPath("$.bank.shortName").value("HDFC"));
    }

    @Test
    @WithMockUser
    void get_shouldReturnAccount() throws Exception {
        when(bankAccountService.get("acct-1")).thenReturn(accountResponse());

        mockMvc.perform(get("/bank-account/acct-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("acct-1"));
    }

    @Test
    @WithMockUser
    void update_shouldReturnUpdatedAccount() throws Exception {
        when(bankAccountService.update(anyString(), any(UpdateBankAccountRequest.class))).thenReturn(accountResponse());

        mockMvc.perform(put("/bank-account/acct-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"nickName\": \"Updated\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("acct-1"));
    }

    @Test
    @WithMockUser
    void delete_shouldReturn204() throws Exception {
        doNothing().when(bankAccountService).delete("acct-1");

        mockMvc.perform(delete("/bank-account/acct-1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void list_shouldReturnPaginated() throws Exception {
        PaginatedResponse<BankAccountResponse> paginated =
            new PaginatedResponse<>(List.of(accountResponse()), false, 20);
        when(bankAccountService.list(anyInt(), anyInt())).thenReturn(paginated);

        mockMvc.perform(get("/bank-account/list").param("page", "0").param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("acct-1"))
            .andExpect(jsonPath("$.size").value(20));
    }
}
