package com.example.Bank.Model;

import com.example.Bank.Enum.CardStatus;
import com.example.Bank.Enum.CardType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cardId;

    @Column(nullable = false)
    private CardType cardType;

    @Column(nullable = false)
    @NotNull
//    @CreditCardNumber(message="Not a valid credit card number")
    private String ccNumber;

    @Column(nullable = false)
    @NotNull
    @Digits(integer = 4, fraction = 0)
    private String password;

//    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
//            message="Must be formatted MM/YY")
//    private String ccExpiration;
//
//    @Digits(integer=3, fraction=0, message="Invalid CVV")
//    private String ccCVV;

    @Column(nullable = false)
    private CardStatus cardStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @OneToOne(mappedBy = "card", fetch = FetchType.EAGER)
    @JsonIgnore
    private Account account;
}
