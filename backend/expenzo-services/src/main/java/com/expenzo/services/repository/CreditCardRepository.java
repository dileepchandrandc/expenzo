package com.expenzo.services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expenzo.services.model.payment.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    List<CreditCard> findByUserId(Integer userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM credit_card cc WHERE cc.id = :id and cc.user_id = :userId")
    Integer checkCreditCardExists(@Param("userId") Integer userId, @Param("id") Integer id);
}
