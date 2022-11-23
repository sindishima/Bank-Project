package com.example.Bank;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Model.Account;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;

public class SchedulerTest extends Date {

    @Test
    public void testScheduler() throws Exception {
        Account account = new Account();
        account.setCreatedDate(LocalDate.now());
        account.setAccountStatus(AccountStatus.CREATED);


        LocalDate date = LocalDate.now();

        System.out.println(account.getCreatedDate().plusDays(3)+"  "+date);


        if (account.getCreatedDate().plusDays(3).isBefore(date)) {
            account.setAccountStatus(AccountStatus.ACTIVE);
        }

        System.out.println(account.getAccountStatus());
    }
}
