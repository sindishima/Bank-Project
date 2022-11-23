package com.example.Bank.Repostory;

import com.example.Bank.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("select t from Transaction as t where t.account.accountId=:accountId")
    List<Transaction> getTransactionByAccount(Integer accountId);
}
