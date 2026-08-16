package com.expenzo.services.paymentcard.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.expenzo.services.paymentcard.repository.model.CreditCard;

@DataJpaTest
class CreditCardRepositoryTest {

    @Autowired
    private CreditCardRepository creditCardRepository;

    private static final String USER_ID = "user-123";

    private CreditCard saveCard(String cardNumber) {
        return creditCardRepository.save(CreditCard.builder()
            .userId(USER_ID)
            .cardNumber(cardNumber)
            .validFrom(LocalDate.of(2026, 1, 1))
            .validTo(LocalDate.of(2029, 12, 31))
            .creditLimit(new BigDecimal("50000.00"))
            .billingDate(5)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .isActive(true)
            .build());
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldReturnCard() {
        CreditCard card = saveCard("1234567890123456");

        Optional<CreditCard> found =
            creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull(card.getId(), USER_ID);

        assertThat(found).isPresent();
        assertThat(found.get().getCardNumber()).isEqualTo("1234567890123456");
        assertThat(found.get().getCreditLimit()).isEqualByComparingTo("50000.00");
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreOtherUser() {
        CreditCard card = saveCard("1111222233334444");

        Optional<CreditCard> found =
            creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull(card.getId(), "other-user");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreSoftDeleted() {
        CreditCard card = saveCard("5555666677778888");
        card.setDeletedAt(OffsetDateTime.now());
        card.setActive(false);
        creditCardRepository.save(card);

        Optional<CreditCard> found =
            creditCardRepository.findByIdAndUserIdAndDeletedAtIsNull(card.getId(), USER_ID);

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUserIdAndDeletedAtIsNull_shouldReturnOnlyUsersCards() {
        saveCard("9999000011112222");
        saveCard("3333444455556666");

        Page<CreditCard> page =
            creditCardRepository.findByUserIdAndDeletedAtIsNull(USER_ID, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }
}
