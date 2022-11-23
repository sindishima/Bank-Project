package com.example.Bank.Repostory;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Transactional
    @Modifying
    @Query(value = "update bank.account set account_status=:status where account_id=:id", nativeQuery = true)
    void updateAccountStatus(Integer id, Integer status);

    @Query("select a from Account as a where a.IBAN=:iban")
    Account findAccountByIban(String iban);

    @Query("select a from Account as a where a.client.id=:clientId")
    List<Account> findAccountByClient(Integer clientId);

    @Query("select a from Account as a where a.accountStatus=:status")
    List<Account> findAccountByStatus(AccountStatus status);
}
