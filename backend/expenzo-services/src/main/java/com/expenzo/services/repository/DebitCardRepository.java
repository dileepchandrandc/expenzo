package com.expenzo.services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.payment.DebitCard;

@Repository
public interface DebitCardRepository extends JpaRepository<DebitCard, Integer> {

    List<DebitCard> findByUserId(Integer userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM debit_card dc WHERE dc.id = :id and dc.user_id = :userId")
    Integer checkDebitCardExists(@Param("userId") Integer userId, @Param("id") Integer id);
}
