package com.expenzo.services.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.expenzo.services.auth.dto.UserAccount;
import com.expenzo.services.auth.repository.ExpenzoUserRepository;
import com.expenzo.services.auth.repository.model.ExpenzoUser;
import com.expenzo.services.auth.request.ChangePasswordRequest;
import com.expenzo.services.auth.request.CreateUserRequest;
import com.expenzo.services.auth.request.UpdateUserRequest;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    private static final String CURRENT_USER_ID = "user-1";

    @Mock
    private ExpenzoUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountService userAccountService;

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

    private ExpenzoUser currentUser() {
        return ExpenzoUser.builder()
            .id(CURRENT_USER_ID)
            .email("dileep@example.com")
            .firstName("Dileep")
            .lastName("Chandran")
            .password("encoded")
            .build();
    }

    @Test
    void create_shouldSaveNewUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@example.com");
        request.setPassword("Password1");
        request.setFirstName("New");
        request.setLastName("User");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("encoded-password");
        when(userRepository.save(any(ExpenzoUser.class))).thenAnswer(inv ->
            ExpenzoUser.builder()
                .id("new-id")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .build()
        );

        UserAccount result = userAccountService.create(request);

        assertThat(result.getUserId()).isEqualTo("new-id");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).save(any(ExpenzoUser.class));
    }

    @Test
    void create_shouldThrow_whenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("dup@example.com");
        request.setPassword("Password1");
        request.setFirstName("Dup");

        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.create(request))
            .isInstanceOf(DataIntegrityViolationException.class);
        verify(userRepository, never()).save(any(ExpenzoUser.class));
    }

    @Test
    void get_shouldReturnCurrentUser() {
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser()));

        UserAccount result = userAccountService.get();

        assertThat(result.getUserId()).isEqualTo(CURRENT_USER_ID);
        assertThat(result.getEmail()).isEqualTo("dileep@example.com");
    }

    @Test
    void get_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.get())
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void update_shouldApplyProvidedFields() {
        ExpenzoUser user = currentUser();
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(ExpenzoUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setMobileNumber("9876543210");

        UserAccount result = userAccountService.update(request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(user.getMobileNumber()).isEqualTo("9876543210");
        // Unchanged fields remain
        assertThat(user.getLastName()).isEqualTo("Chandran");
    }

    @Test
    void changePassword_shouldUpdateWhenOldMatches() {
        ExpenzoUser user = currentUser();
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass1", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass1")).thenReturn("new-encoded");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("OldPass1");
        request.setNewPassword("NewPass1");

        userAccountService.changePassword(request);

        assertThat(user.getPassword()).isEqualTo("new-encoded");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrow_whenOldPasswordIncorrect() {
        ExpenzoUser user = currentUser();
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("Wrong1");
        request.setNewPassword("NewPass1");

        assertThatThrownBy(() -> userAccountService.changePassword(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Old password is incorrect");
        verify(userRepository, never()).save(any(ExpenzoUser.class));
    }

    @Test
    void delete_shouldRemoveCurrentUser() {
        ExpenzoUser user = currentUser();
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(user));

        userAccountService.delete();

        verify(userRepository).delete(user);
    }
}
