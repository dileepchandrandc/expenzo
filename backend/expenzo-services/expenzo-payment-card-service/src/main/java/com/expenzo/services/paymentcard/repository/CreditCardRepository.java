package com.expenzo.services.paymentcard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.paymentcard.repository.model.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, String> {

    Optional<CreditCard> findByIdAndUserIdAndDeletedAtIsNull(String id, String userId);

    Page<CreditCard> findByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);
}
