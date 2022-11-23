package com.example.Bank.Scheduler;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Enum.CardStatus;
import com.example.Bank.Model.Account;
import com.example.Bank.Model.Card;
import com.example.Bank.Repostory.AccountRepository;
import com.example.Bank.Repostory.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@EnableScheduling
@Component
public class Schedulers {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CardRepository cardRepository;

    private static final Logger log = LoggerFactory.getLogger(Schedulers.class);

    @Scheduled(cron = "5 * * * * *")
    void makeAccountStatusActive(){
        List<Account> accountList = accountRepository.findAccountByStatus(AccountStatus.APPROVED);
        log.info("Account scheduler started at: {}", LocalDate.now());
        for(Account account : accountList){
            if (account.getCreatedDate().plusDays(3).isBefore(LocalDate.now())) {
                account.setAccountStatus(AccountStatus.ACTIVE);
            }
        }
    }

    @Scheduled(cron = "30 * * * * *")
    void makeCardStatusActive(){
        List<Card> cardList = cardRepository.findWaitingForApprovalCards(CardStatus.APPROVED);

        log.info("Card scheduler started at: {}", LocalDate.now());
        for(Card card : cardList){
            if (card.getCreatedDate().plusDays(3).isBefore(LocalDate.now())) {
                card.setCardStatus(CardStatus.ACTIVE);
            }
        }
    }
}
