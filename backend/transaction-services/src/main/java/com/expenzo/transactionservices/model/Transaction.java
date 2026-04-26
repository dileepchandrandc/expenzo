package com.expenzo.transactionservices.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.expenzo.transactionservices.enums.PaymentChannel;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private BigDecimal amount;
    private String title;
    private String description;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private PaymentChannel sourceType;
    private Integer sourceId;
    private PaymentChannel destType;
    private Integer destId;
}
