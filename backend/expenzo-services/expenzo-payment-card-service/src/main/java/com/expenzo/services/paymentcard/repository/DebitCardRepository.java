package com.expenzo.services.paymentcard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.paymentcard.repository.model.DebitCard;

@Repository
public interface DebitCardRepository extends JpaRepository<DebitCard, String> {

    Optional<DebitCard> findByIdAndUserIdAndDeletedAtIsNull(String id, String userId);

    Page<DebitCard> findByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);
}
