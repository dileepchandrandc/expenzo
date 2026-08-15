package com.expenzo.services.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.expenzo.services.auth.config.JwtFilterChain;
import com.expenzo.services.auth.dto.UserAccount;
import com.expenzo.services.auth.request.ChangePasswordRequest;
import com.expenzo.services.auth.request.CreateUserRequest;
import com.expenzo.services.auth.request.UpdateUserRequest;
import com.expenzo.services.auth.service.UserAccountService;

@WebMvcTest(UserAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private JwtFilterChain jwtFilterChain;

    @Test
    @WithMockUser
    void get_shouldReturnCurrentUser() throws Exception {
        UserAccount account = new UserAccount();
        account.setUserId("user-1");
        account.setFirstName("Dileep");
        account.setLastName("Chandran");
        account.setEmail("dileep@example.com");

        when(userAccountService.get()).thenReturn(account);

        mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("user-1"))
            .andExpect(jsonPath("$.email").value("dileep@example.com"));
    }

    @Test
    @WithMockUser
    void update_shouldReturnUpdatedUser() throws Exception {
        UserAccount account = new UserAccount();
        account.setUserId("user-1");
        account.setFirstName("Updated");
        account.setEmail("dileep@example.com");

        when(userAccountService.update(any(UpdateUserRequest.class))).thenReturn(account);

        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"firstName\": \"Updated\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @WithMockUser
    void changePassword_shouldReturn204() throws Exception {
        doNothing().when(userAccountService).changePassword(any(ChangePasswordRequest.class));

        mockMvc.perform(put("/user/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"oldPassword\": \"OldPass1\", \"newPassword\": \"NewPass1\" }"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_shouldReturn204() throws Exception {
        doNothing().when(userAccountService).delete();

        mockMvc.perform(delete("/user"))
            .andExpect(status().isNoContent());
    }

    @Test
    void create_shouldReturn201() throws Exception {
        UserAccount account = new UserAccount();
        account.setUserId("new-user");
        account.setFirstName("New");
        account.setEmail("new@example.com");

        when(userAccountService.create(any(CreateUserRequest.class))).thenReturn(account);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"new@example.com\", \"password\": \"Password1\", \"firstName\": \"New\" }"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value("new-user"));
    }
}
