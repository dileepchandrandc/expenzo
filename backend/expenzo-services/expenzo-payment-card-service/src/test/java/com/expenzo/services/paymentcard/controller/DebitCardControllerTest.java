package com.expenzo.services.paymentcard.controller;

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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.expenzo.services.common.PaginatedResponse;
import com.expenzo.services.paymentcard.dto.DebitCardResponse;
import com.expenzo.services.paymentcard.request.CreateDebitCardRequest;
import com.expenzo.services.paymentcard.request.UpdateDebitCardRequest;
import com.expenzo.services.paymentcard.service.DebitCardService;

@ExtendWith(MockitoExtension.class)
class DebitCardControllerTest {

    @Mock
    private DebitCardService debitCardService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DebitCardController(debitCardService)).build();
    }

    private DebitCardResponse cardResponse() {
        DebitCardResponse response = new DebitCardResponse();
        response.setId("card-1");
        response.setUserId("user-1");
        response.setBankAccountId("acct-1");
        response.setCardNumber("1234567890123456");
        response.setValidFrom(LocalDate.of(2026, 1, 1));
        response.setValidTo(LocalDate.of(2029, 12, 31));
        response.setCreatedAt(OffsetDateTime.now());
        response.setUpdatedAt(OffsetDateTime.now());
        response.setActive(true);
        return response;
    }

    @Test
    void create_shouldReturn201() throws Exception {
        when(debitCardService.create(any(CreateDebitCardRequest.class))).thenReturn(cardResponse());

        mockMvc.perform(post("/debit-card")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"bankAccountId\": \"acct-1\", \"cardNumber\": \"1234567890123456\", \"validTo\": \"2029-12\" }"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("card-1"))
            .andExpect(jsonPath("$.bankAccountId").value("acct-1"));
    }

    @Test
    void get_shouldReturnCard() throws Exception {
        when(debitCardService.get("card-1")).thenReturn(cardResponse());

        mockMvc.perform(get("/debit-card/card-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("card-1"));
    }

    @Test
    void update_shouldReturnUpdatedCard() throws Exception {
        when(debitCardService.update(anyString(), any(UpdateDebitCardRequest.class))).thenReturn(cardResponse());

        mockMvc.perform(put("/debit-card/card-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"nickName\": \"Updated\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("card-1"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(debitCardService).delete("card-1");

        mockMvc.perform(delete("/debit-card/card-1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void list_shouldReturnPaginated() throws Exception {
        PaginatedResponse<DebitCardResponse> paginated =
            new PaginatedResponse<>(List.of(cardResponse()), false, 20);
        when(debitCardService.list(anyInt(), anyInt())).thenReturn(paginated);

        mockMvc.perform(get("/debit-card/list").param("page", "0").param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("card-1"))
            .andExpect(jsonPath("$.size").value(20));
    }
}
