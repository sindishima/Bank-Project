package com.example.Bank.Model;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Enum.AccountType;
import com.example.Bank.Enum.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int accountId;

    @Column(nullable = false, unique = true)
    public String IBAN;

    @Column(nullable = false)
    @NotNull
    private Currency currency;

    @Column(nullable = false)
    @NotNull
    private double balance;

    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private double interest;

    private AccountStatus accountStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @OneToOne
    @JoinTable(name = "account_card",
            joinColumns =
                    {@JoinColumn(name = "account_no")},
            inverseJoinColumns =
                    {@JoinColumn(name = "card_id")})
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private User client;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Transaction> transactionList = new ArrayList<>();


    public Boolean hasCard(){
        if(card !=null){
            return true;
        }
        else {
            return false;
        }
    }
}
