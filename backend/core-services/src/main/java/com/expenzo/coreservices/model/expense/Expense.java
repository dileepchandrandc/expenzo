package com.expenzo.coreservices.model.expense;

import com.expenzo.coreservices.model.payment.BankAccount;
import com.expenzo.coreservices.model.payment.CreditCard;
import com.expenzo.coreservices.model.payment.DebitCard;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expense")
public class Expense {

    @Id
    private Integer id;
    private Integer userId;

    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDateTime spentOn;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;
    @ManyToOne
    @JoinColumn(name = "debit_card_id")
    private DebitCard debitCard;
}
