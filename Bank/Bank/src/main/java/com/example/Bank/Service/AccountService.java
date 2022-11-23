package com.example.Bank.Service;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Enum.AccountType;
import com.example.Bank.Enum.Role;
import com.example.Bank.Exceptions.NoAuthorizationException;
import com.example.Bank.Exceptions.ResourceNotFoundException;
import com.example.Bank.Model.Account;
import com.example.Bank.Model.User;
import com.example.Bank.Others.IBAN;
import com.example.Bank.Repostory.AccountRepository;
import com.example.Bank.Repostory.CardRepository;
import com.example.Bank.Repostory.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserService userService;

    @Autowired
    CardRepository cardRepository;


    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }


    public Account getAccount(Integer accountId){
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> account = accountRepository.findById(accountId);

        if(loggedUser!=null && !account.isEmpty()){
            Account account1 = account.get();
            if(loggedUser.getRole().equals(Role.TELLER)){
                return account1;
            }
            else if(loggedUser.getId()==account1.getClient().getId()){
                return account1;
            }
            else{
                log.error(String.format("User with id %s is not authorized to see account of user with id %s", loggedUser.getId(), account1.getClient().getId()));
                throw new NoAuthorizationException("This user is not authorized");
            }
        }
        else {
            log.error(String.format("Account with id %s doesn't exist", accountId));
            throw new ResourceNotFoundException("Account or user doesn't exist");
        }
    }


    public List<Account> getAllAccountsOfAClient(Integer clientId){
        User loggedUser = userService.getCurrentLoggedUser();

        if(loggedUser!=null){
            if(clientId==null && loggedUser.getRole().equals(Role.CLIENT)){
                return accountRepository.findAccountByClient(loggedUser.getId());
            }
            else if(clientId!=null && loggedUser.getRole().equals(Role.TELLER)){
                Optional<User> optionalUser = userRepository.findById(clientId);
                if(!optionalUser.isEmpty() && optionalUser.get().getRole().equals(Role.CLIENT) ) {
                    return accountRepository.findAccountByClient(clientId);
                }
                else{
                    throw new ResourceNotFoundException("This client doesn't exist or does not have the role CLIENT");
                }
            }
            else {
                log.error(String.format("User with id %s is not authorized to see account of user with id %s", loggedUser.getId(), clientId));
                throw new NoAuthorizationException("This user is not authorized");
            }
        }
        else {
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }

    public Account createAccount(Account account) throws Exception {
        User loggedUser = userService.getCurrentLoggedUser();
        if(loggedUser.getRole().equals(Role.CLIENT)) {
                account.setAccountType(AccountType.CURRENT);
                account.setInterest(0);
                account.setClient(loggedUser);
                account.setAccountStatus(AccountStatus.CREATED);
                account.setCreatedDate(LocalDate.now());
                IBAN iban = new IBAN();
                account.setIBAN(iban.generateIban());
                System.out.println("IBAN "+iban.generateIban());

                loggedUser.getAccountList().add(account);
                log.info("Account is submitted");
                return accountRepository.save(account);
        }
        else {
            log.error(String.format("User with id %s is not authorized", loggedUser.getId()));
            throw new NoAuthorizationException("This user is not authorized");
        }
    }


    public String deleteAccount(Integer accountId){
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if(!accountOptional.isEmpty()) {
            Account account = accountOptional.get();
            if (loggedUser!=null && loggedUser.getAccountList().contains(account)) {
                if(account.getAccountType().equals(AccountType.TECHNICAL)){
                    accountRepository.deleteById(accountId);
                    if(account.hasCard()) {
                        cardRepository.deleteById(account.getCard().getCardId());
                    }
                    log.info(String.format("Account with id %s is deleted", accountId));
                    return "Account deleted";
                }
                else {
//                    account.setCard(null);
                    accountRepository.deleteById(accountId);
                    log.info(String.format("Account with id %s is deleted", accountId));
                    return "Account deleted";
                }
            } else {
                throw new NoAuthorizationException("THis user is not authorized to delete this account");
            }
        }
        else {
            log.error(String.format("Account with id %s doesn't exist", accountId));
            throw new ResourceNotFoundException("This account doesn't exist");
        }
    }


    public String approveOrDeclineAccount(Integer accountId, AccountStatus accountStatus){
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if(accountOptional.isEmpty()){
            log.error(String.format("Account with id %s doesn't exist", accountId));
            throw new ResourceNotFoundException("This account doesn't exist");
        }
        else {
            if(loggedUser!=null){
                if(loggedUser.getRole().equals(Role.TELLER)) {
                    Account account = accountOptional.get();
                    if(accountStatus.equals(AccountStatus.APPROVED) || accountStatus.equals(AccountStatus.DISAPPROVED)) {
                        if (!account.getAccountStatus().equals(accountStatus) && account.getAccountStatus().equals(AccountStatus.CREATED)) {
                            int status = accountStatus.ordinal();    //convert from enum to int
                            accountRepository.updateAccountStatus(accountId, status);
                            accountRepository.save(account);
                            return "Status changed to " + accountStatus;
                        } else {
                            throw new NoAuthorizationException("User is not authorized to change the account status to new because both statuses are same");
                        }
                    }
                    else {
                        throw new ResourceNotFoundException("This status doesn't exist");
                    }
                }
                else{
                    log.error(String.format("User with id %s is not authorized", loggedUser.getId()));
                    throw new NoAuthorizationException("The user must be a teller in order to update the client status");
                }
            }
            else {
                throw new ResourceNotFoundException("This user doesn't exist");
            }
        }
    }


    public Account activateAccount(Integer accountId){
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(loggedUser!=null){
            if(!accountOptional.isEmpty()){
                Account account = accountOptional.get();
                if(account.getAccountStatus().equals(AccountStatus.APPROVED)){
                    account.setAccountStatus(AccountStatus.ACTIVE);
                    return accountRepository.save(account);
                }
                else {
                    throw new NoAuthorizationException("Not authorized to change account status");
                }
            }
            else {
                throw new ResourceNotFoundException("This account does not exist");
            }
        }
        else{
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }


    public List<Account> getAccountsByStatus(AccountStatus accountStatus){
        User loggedUser = userService.getCurrentLoggedUser();

        if(loggedUser.getRole().equals(Role.TELLER)){
            if(accountStatus!=null){
                return accountRepository.findAccountByStatus(accountStatus);
            }
            else {
                throw new ResourceNotFoundException("Account status is missing");
            }
        }
        else {
            throw new NoAuthorizationException("This user is not authorized");
        }
    }
}
