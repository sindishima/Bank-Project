package com.example.Bank.Service;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Enum.CardStatus;
import com.example.Bank.Enum.CardType;
import com.example.Bank.Enum.Role;
import com.example.Bank.Exceptions.NoAuthorizationException;
import com.example.Bank.Exceptions.ResourceNotFoundException;
import com.example.Bank.Model.Account;
import com.example.Bank.Model.Transaction;
import com.example.Bank.Model.User;
import com.example.Bank.Repostory.AccountRepository;
import com.example.Bank.Repostory.TransactionRepository;
import com.example.Bank.Repostory.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }


    public Transaction getTransaction(Integer id) {
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);

        if (loggedUser != null && !transactionOptional.isEmpty()) {
            if (loggedUser.getRole().equals(Role.CLIENT)) {
                List<Transaction> transactionList = new ArrayList<>();
                for (Account a : loggedUser.getAccountList()) {
                    transactionList.addAll(a.getTransactionList());
                }
                if(transactionList.contains(transactionOptional.get())){
                    return transactionOptional.get();
                }
                else {
                    throw new NoAuthorizationException("This transaction is not part of this client transaction list");
                }
            } else if (loggedUser.getRole().equals(Role.TELLER)) {
                return transactionOptional.get();
            } else {
                throw new NoAuthorizationException("this user is not authorized");
            }
        }
        else{
                throw new ResourceNotFoundException("This transaction doesn't exist");
            }
    }


    public List<Transaction> getTransactionsFromOneAccount(Integer accountId) {
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if(loggedUser!=null && !optionalAccount.isEmpty()){
            if(loggedUser.getRole().equals(Role.CLIENT)){
                List<Account> accountList = new ArrayList<>();
                accountList.addAll(loggedUser.getAccountList());
                if(accountList.contains(optionalAccount.get())){
                    return transactionRepository.getTransactionByAccount(accountId);
                }
                else {
                    throw new NoAuthorizationException("This user cannot access this account");
                }
            }
            else if(loggedUser.getRole().equals(Role.TELLER)){
                return transactionRepository.getTransactionByAccount(accountId);
            }
            else {
                throw new NoAuthorizationException("This user is not authorized");
            }
        }
        else {
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }



    public List<Transaction> getAllClientTransaction(Integer clientId) {
        User loggedUser = userService.getCurrentLoggedUser();

        if (loggedUser != null) {
            if (clientId == null && loggedUser.getRole().equals(Role.CLIENT)) {
                List<Transaction> list = new ArrayList();
                for (Account a : loggedUser.getAccountList()) {
                    list.addAll(a.getTransactionList());
                }
                return list;
            } else if (clientId != null && loggedUser.getRole().equals(Role.TELLER)) {
                Optional<User> clientOptional = userRepository.findById(clientId);

                if (!clientOptional.isEmpty()) {
                    User client = clientOptional.get();
                    List<Transaction> list = new ArrayList();
                    for (Account a : client.getAccountList()) {
                        list.addAll(a.getTransactionList());
                    }
                    return list;
                } else {
                    throw new ResourceNotFoundException("this client doesn't exist");
                }
            } else {
                throw new NoAuthorizationException("This user is not authorized");
            }
        } else {
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }


    public Transaction createTransaction(String iban, Integer amount, Integer accountId) {
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> senderAccountOptional = accountRepository.findById(accountId);
        Account receiverAccount = accountRepository.findAccountByIban(iban);

        if(!senderAccountOptional.isEmpty() && receiverAccount!=null) {
            Account senderAccount = senderAccountOptional.get();

            if (loggedUser.getAccountList().contains(senderAccount) && loggedUser.getId() != receiverAccount.getClient().getId()) {  //cannot send money to yourself

                if (senderAccount.hasCard() && receiverAccount.hasCard()) {
                    if (senderAccount.getAccountStatus().equals(AccountStatus.ACTIVE) && senderAccount.getAccountStatus().equals(AccountStatus.ACTIVE)
                            && senderAccount.getCard().getCardStatus().equals(CardStatus.ACTIVE) && receiverAccount.getCard().getCardStatus().equals(CardStatus.ACTIVE)) {
                        if (senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
                            Transaction senderTransaction = new Transaction();
                            Transaction receiverTransaction = new Transaction();
                            if (senderAccount.getBalance() > amount) {
                                senderTransaction.setAccount(senderAccount);
                                senderTransaction.setAmount(-amount);   //(-)
                                senderTransaction.setCurrency(senderAccount.getCurrency());
                                senderTransaction.setCardType(senderAccount.getCard().getCardType());
                                senderTransaction.setIBAN(iban);

                                receiverTransaction.setAccount(receiverAccount);
                                receiverTransaction.setAmount(+amount);   //(+)
                                receiverTransaction.setCurrency(receiverAccount.getCurrency());
                                receiverTransaction.setCardType(receiverAccount.getCard().getCardType());
                                receiverTransaction.setIBAN(senderAccount.getIBAN());

                                Double newAmount = amount + amount * senderAccount.getInterest() / 100;
                                senderAccount.setBalance(senderAccount.getBalance() - newAmount);
                                senderAccount.getTransactionList().add(senderTransaction);
                                receiverAccount.setBalance(receiverAccount.getBalance() + amount);
                                receiverAccount.getTransactionList().add(receiverTransaction);

                                transactionRepository.save(senderTransaction);
                                return transactionRepository.save(receiverTransaction);

                            } else {
                                if (senderAccount.getCard().getCardType().equals(CardType.CREDIT)) {
                                    senderTransaction.setAccount(senderAccount);
                                    senderTransaction.setAmount(amount);   //(-)
                                    senderTransaction.setCurrency(senderAccount.getCurrency());
                                    senderTransaction.setCardType(senderAccount.getCard().getCardType());
                                    senderTransaction.setIBAN(iban);

                                    receiverTransaction.setAccount(receiverAccount);
                                    receiverTransaction.setAmount(amount);   //(+)
                                    receiverTransaction.setCurrency(receiverAccount.getCurrency());
                                    receiverTransaction.setCardType(receiverAccount.getCard().getCardType());
                                    receiverTransaction.setIBAN(senderAccount.getIBAN());

                                    Double newAmount = amount + amount * senderAccount.getInterest() / 100;
                                    senderAccount.setBalance(senderAccount.getBalance() - newAmount);
                                    senderAccount.getTransactionList().add(senderTransaction);
                                    receiverAccount.setBalance(receiverAccount.getBalance() + amount);
                                    receiverAccount.getTransactionList().add(receiverTransaction);

                                    transactionRepository.save(senderTransaction);
                                    return transactionRepository.save(receiverTransaction);
                                } else {
                                    log.error(String.format("User with id %s doesn't have enough money"), loggedUser.getId());
                                    throw new ResourceNotFoundException("Not enough money to perform this transaction");
                                }
                            }
                        } else {
                            throw new ResourceNotFoundException("Both clients should have the same currency in order to perform the transaction");
                        }
                    } else {
                        throw new ResourceNotFoundException("Both cards and accounts must have an ACTIVE state in order to perform transaction");
                    }
                } else {
                    throw new ResourceNotFoundException("Each account must be linked with a card");
                }
            } else {
                throw new NoAuthorizationException("This user is not authorized");
            }
        }else {
            throw new ResourceNotFoundException("This accounts doesn't exist");
        }
    }
}
