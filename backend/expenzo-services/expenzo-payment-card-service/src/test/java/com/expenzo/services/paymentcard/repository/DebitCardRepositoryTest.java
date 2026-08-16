package com.expenzo.services.paymentcard.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.expenzo.services.paymentcard.repository.model.DebitCard;

@DataJpaTest
class DebitCardRepositoryTest {

    @Autowired
    private DebitCardRepository debitCardRepository;

    private static final String USER_ID = "user-123";

    private DebitCard saveCard(String cardNumber) {
        return debitCardRepository.save(DebitCard.builder()
            .userId(USER_ID)
            .bankAccountId("acct-1")
            .cardNumber(cardNumber)
            .validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2029, 12, 31))
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build());
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldReturnCard() {
        DebitCard card = saveCard("1234567890123456");

        Optional<DebitCard> found =
            debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull(card.getId(), USER_ID);

        assertThat(found).isPresent();
        assertThat(found.get().getCardNumber()).isEqualTo("1234567890123456");
        assertThat(found.get().getBankAccountId()).isEqualTo("acct-1");
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreOtherUser() {
        DebitCard card = saveCard("1111222233334444");

        Optional<DebitCard> found =
            debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull(card.getId(), "other-user");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreSoftDeleted() {
        DebitCard card = saveCard("5555666677778888");
        card.setDeletedAt(OffsetDateTime.now());
        card.setActive(false);
        debitCardRepository.save(card);

        Optional<DebitCard> found =
            debitCardRepository.findByIdAndUserIdAndDeletedAtIsNull(card.getId(), USER_ID);

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUserIdAndDeletedAtIsNull_shouldReturnOnlyUsersCards() {
        saveCard("9999000011112222");
        saveCard("3333444455556666");

        Page<DebitCard> page =
            debitCardRepository.findByUserIdAndDeletedAtIsNull(USER_ID, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }
}
