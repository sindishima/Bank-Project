package com.example.Bank.Model;

import com.example.Bank.Enum.CardType;
import com.example.Bank.Enum.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int transactionId;

    @Column(nullable = false)
    @NotNull
    private int amount;

    @Column(nullable = false)
    @NotNull
    private Currency currency;

    @Column(nullable = false)
    @NotNull
    private CardType cardType;

    @Column(nullable = false)
    @NotNull
    private String IBAN;   //other person iban

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;
}
