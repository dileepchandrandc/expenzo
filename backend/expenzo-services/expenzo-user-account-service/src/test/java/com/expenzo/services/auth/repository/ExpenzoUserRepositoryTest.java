package com.expenzo.services.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.expenzo.services.auth.repository.model.ExpenzoUser;

@DataJpaTest
class ExpenzoUserRepositoryTest {

    @Autowired
    private ExpenzoUserRepository userRepository;

    private ExpenzoUser saveUser(String email) {
        return userRepository.save(ExpenzoUser.builder()
            .email(email)
            .firstName("Dileep")
            .lastName("Chandran")
            .password("encoded-password")
            .build());
    }

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        saveUser("dileep.test@example.com");

        Optional<ExpenzoUser> found = userRepository.findByEmail("dileep.test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("dileep.test@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotExists() {
        Optional<ExpenzoUser> found = userRepository.findByEmail("missing@example.com");

        assertThat(found).isNotPresent();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenExists() {
        saveUser("exists@example.com");

        boolean exists = userRepository.existsByEmail("exists@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenNotExists() {
        boolean exists = userRepository.existsByEmail("nope@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldPersistUser_withGeneratedId() {
        ExpenzoUser saved = saveUser("persist@example.com");

        assertThat(saved.getId()).isNotBlank();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }
}
