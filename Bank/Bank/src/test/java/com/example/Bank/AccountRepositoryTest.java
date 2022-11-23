package com.example.Bank;


import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Enum.CardType;
import com.example.Bank.Enum.Role;
import com.example.Bank.Model.Account;
import com.example.Bank.Model.Card;
import com.example.Bank.Model.User;
import com.example.Bank.Repostory.AccountRepository;
import com.example.Bank.Repostory.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@DataJpaTest  //when testing repositories
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

//    @AfterEach
//    void tearDown(){
//        accountRepository.deleteAll();
//    }

    @Test
    void findAccountBasedOnIBAN() {

        User client = new User();
        client.setId(1);
        client.setPassword("SDds");
        client.setRole(Role.CLIENT);
        client.setUsername("SASASAS");

        Account account = new Account();
        account.setCreatedDate(LocalDate.now());
        account.setAccountId(2);
        account.setIBAN("AL96CJQW1234567905");
        account.setInterest(0);
        account.setClient(client);
        account.setAccountStatus(AccountStatus.ACTIVE);

        Card card = new Card();
        card.setCreatedDate(LocalDate.now());
        card.setCcNumber("#@222242");
        card.setCardType(CardType.DEBIT);
        card.setCardId(323);
        card.setAccount(account);
        client.getAccountList().add(account);


        try {
            userRepository.save(client);
        }catch (Exception e){
            throw e;
        }


        Account exist = accountRepository.findAccountByIban("AL96CJQW1234567905");

        System.out.println(exist);
        assertFalse(exist==null);
    }
}
