package com.example.Bank.Service;

import com.example.Bank.Enum.Currency;
import com.example.Bank.Enum.*;
import com.example.Bank.Exceptions.NoAuthorizationException;
import com.example.Bank.Exceptions.ResourceNotFoundException;
import com.example.Bank.Model.Account;
import com.example.Bank.Model.Card;
import com.example.Bank.Model.User;
import com.example.Bank.Others.IBAN;
import com.example.Bank.Property.AppProperties;
import com.example.Bank.Repostory.AccountRepository;
import com.example.Bank.Repostory.CardRepository;
import com.example.Bank.Repostory.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CardService {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AppProperties properties;

    @Autowired
    UserService userService;


    private static final Logger log = LoggerFactory.getLogger(Card.class);

    public List<Card> getAllCards() {
        User loggedUser = userService.getCurrentLoggedUser();
        List<Card> cardList = new ArrayList<>();

        if(loggedUser!=null) {
            if (loggedUser.getRole().equals(Role.CLIENT)) {
                List<Account> accountList = accountRepository.findAccountByClient(loggedUser.getId());
                for (Account a : accountList) {
                    if(a.getCard()!=null) {
                        cardList.add(a.getCard());
                    }
                }
                if(cardList.isEmpty()){
                    log.info("You don't have any cards");
                    throw new ResourceNotFoundException("You don't have any cards");
                }
                else {
                    return cardList;
                }
            }
//            else if(clientId!=null && loggedUser.getRole().equals(Role.TELLER)) {
//                if(!userRepository.findById(clientId).isEmpty()) {
//                    List<Account> accountList = accountRepository.findAccountByClient(clientId);
//                    for (Account a : accountList) {
//                        if(a.getCard()!=null) {
//                            if(a.getCard()!=null) {
//                                cardList.add(a.getCard());
//                            }
//                        }
//                    }
//                    if(cardList.isEmpty()){
//                        log.info(String.format("User with id %s doesn't have any cards", clientId));
//                        throw new ResourceNotFoundException("This user doesn't have any cards");
//                    }
//                    else {
//                        return cardList;
//                    }
//                }
//                else {
//                    log.error(String.format("User with id %s isn't not valid", clientId));
//                    throw new ResourceNotFoundException("This client id id not valid");
//                }
//            }
            else {
                log.error(String.format("User with id %s isn't authorized", loggedUser.getId()));
                throw new NoAuthorizationException("User is not authorized");
            }
        }
        else {
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }


    public Card getCard(Integer accountId){
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Account> account = accountRepository.findById(accountId);
        if(loggedUser!=null && !account.isEmpty()){
            Account account1 = account.get();
            if(loggedUser.getAccountList().contains(account1)) {
                return account.get().getCard();
            }
            else {
                throw new NoAuthorizationException("This account does not belong to this user");
            }
        }
        else {
            throw new ResourceNotFoundException("This account does not exist");
        }
    }


    public Card createDebitCard(Integer accountId, Card card) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        User loggedUser = userService.getCurrentLoggedUser();

        if (!accountOptional.isEmpty() && loggedUser != null) {
            Account account = accountOptional.get();
            if (!account.hasCard() && loggedUser.getAccountList().contains(account)) {  //this account should not have any card in order to create one
                if (account.getAccountStatus().equals(AccountStatus.ACTIVE) && account.getAccountType().equals(AccountType.CURRENT)) {
                    Card newCard = new Card();
                    newCard.setCardStatus(CardStatus.CREATED);
                    newCard.setCardType(CardType.DEBIT);
                    newCard.setAccount(account);
                    newCard.setCcNumber(card.getCcNumber());
                    newCard.setPassword(card.getPassword());
                    newCard.setCreatedDate(LocalDate.now());

                    account.setCard(newCard);
                    log.info(String.format("Card with id %s is submitted for approval", card.getCardId()));
                    return cardRepository.save(newCard);
                }
                else {
                    log.error(String.format("Account with id %s is not active", account.getAccountId()));
                    throw new ResourceNotFoundException("This account is not yet ACTIVE");
                }
            }
            else {
                log.error(String.format("Account with id %s already has a card", account.getAccountId()));
                throw new ResourceNotFoundException("This account already has a card");            }
        }
        else {
            throw new ResourceNotFoundException("This account or client does not exist");
        }
    }


    public Card createCreditCard(Integer monthlyIncome, Card card, Currency currency, Integer balance) throws Exception {
        User client = userService.getCurrentLoggedUser();

        if (monthlyIncome < properties.getCardIncome()) {
            log.error("Not enough income");
            throw new ResourceNotFoundException("This incomes are not enough");
        } else {
            if (client != null) {
                Card newCard = new Card();
                Account account = new Account();

                account.setAccountType(AccountType.TECHNICAL);
                account.setCurrency(currency);
                account.setBalance(balance);
                account.setAccountStatus(AccountStatus.CREATED);
                account.setClient(client);
                account.setCreatedDate(LocalDate.now());
                account.setInterest(0);
                IBAN iban = new IBAN();
                account.setIBAN(iban.generateIban());
                accountRepository.save(account);

                newCard.setCardStatus(CardStatus.CREATED);
                newCard.setCardType(CardType.CREDIT);
                newCard.setCcNumber(card.getCcNumber());
                newCard.setPassword(card.getPassword());
                newCard.setCreatedDate(LocalDate.now());

                account.setCard(newCard);
                newCard.setAccount(account);

                return cardRepository.save(newCard);
            } else {
                log.error(String.format("User with id %s doesn't exist", client.getId()));
                throw new ResourceNotFoundException("This user doesnt exist");
            }
        }
    }


    public Set<Integer> getCardByStatus(CardStatus status) {
        if (status == null) {
            throw new ResourceNotFoundException("Status not found");
        } else {
            List<Card> cardList = cardRepository.findWaitingForApprovalCards(status);
            Set<Integer> idSet = new HashSet<>();
            for(Card card : cardList){
                idSet.add(card.getCardId());
            }
            return idSet;
        }
    }


    public Card approveOrDeclineCard(Integer cardId, Double percentOfInterest, CardStatus cardStatus) {    //TODO: both credit and debit
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if(!cardOptional.isEmpty()) {
            Card card = cardOptional.get();
            if (getCardByStatus(CardStatus.CREATED).contains(card)) {
                if (cardStatus.equals(CardStatus.APPROVED) || cardStatus.equals(CardStatus.DISAPPROVED)) {
                    if (card.getCardType().equals(CardType.CREDIT)) {
                        card.getAccount().setAccountStatus(AccountStatus.APPROVED);
                        card.getAccount().setInterest(percentOfInterest);
                        card.setCardStatus(CardStatus.APPROVED);
                        log.info(String.format("Card with id %s is approved", cardId));
                        return cardRepository.save(card);
                    } else if (card.getCardType().equals(CardType.DEBIT)) {
                        card.setCardStatus(CardStatus.APPROVED);
                        log.info(String.format("Card with id %s is approved", cardId));
                        return cardRepository.save(card);
                    } else {
                        log.error(String.format("Card with id %s doesn't have a card type", cardId));
                        throw new ResourceNotFoundException("Each card must have a card type");
                    }
                } else {
                    throw new ResourceNotFoundException("This card status doesn't exist");
                }
            }
            else {
                log.error(String.format("Card with id %s doesn't exist", cardId));
                throw new ResourceNotFoundException("This card doesn't exist");
            }
        }
        else {
            throw new ResourceNotFoundException("This card doesn't exist");
        }
    }


    public Card activateCard(Integer cardId){
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if(!cardOptional.isEmpty()){
            Card card = cardOptional.get();
            card.setCardStatus(CardStatus.ACTIVE);
            return cardRepository.save(card);
        }
        else {
            throw new ResourceNotFoundException("This card doesn't exist");
        }
    }

    public Card deposit(Integer cardId, Integer amount) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (!cardOptional.isEmpty() && cardOptional.get().getCardStatus().equals(CardStatus.ACTIVE)) {
            Card card = cardOptional.get();
            if(card.getAccount().getAccountStatus().equals(AccountStatus.ACTIVE)) {
                card.getAccount().setBalance(card.getAccount().getBalance() + amount);
                log.info(String.format("Money are now deposited at card with id %s", card.getCardId()));
                return cardRepository.save(card);
            }
            else{
                throw new NoAuthorizationException("This account is not ACTIVE yet");
            }
        } else {
            log.error(String.format("Card with id %s doesn't exist or is not active", cardId));
            throw new ResourceNotFoundException(String.format("Card with id: %s doesn't exist or is not active", cardId));
        }
    }


    public Card withdraw(Integer cardId, Integer amount) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (!cardOptional.isEmpty()) {
            Card card = cardOptional.get();
            if(card.getAccount().getBalance()>amount) {
                if (card.getCardType().equals(CardType.DEBIT)) {
                    if (card.getAccount().getBalance() > amount) {
                        card.getAccount().setBalance(card.getAccount().getBalance() - amount);
                        log.info(String.format("Money are now withdraw from card with id %s", cardId));
                        return cardRepository.save(card);
                    } else {
                        log.error(String.format("Card with id %s doesn't have enough money", cardId));
                        throw new ResourceNotFoundException("Not enough money");
                    }
                } else if (card.getCardType().equals(CardType.CREDIT)) {
                    card.getAccount().setBalance(card.getAccount().getBalance() - amount);
                    log.info(String.format("Money are now withdraw from card with id %s", cardId));
                    return cardRepository.save(card);
                } else {
                    log.error(String.format("Card with id %s doesn't have a valid card type", cardId));
                    throw new ResourceNotFoundException("Please enter a valid card type");
                }
            }
            else {
                throw new ResourceNotFoundException("Not enough money");
            }
        } else {
            log.error(String.format("Card with id %s doesn't exist", cardId));
            throw new ResourceNotFoundException("This card doesn't exist");
        }
    }


    public String deleteCard(Integer cardId) {
        User loggedUser = userService.getCurrentLoggedUser();
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (loggedUser != null) {
            if (!cardOptional.isEmpty()) {
                Card card = cardOptional.get();
                List<Card> list = new ArrayList();

                for(Account a : loggedUser.getAccountList()){
                    if(a.hasCard()){
                        list.add(a.getCard());
                    }
                }

                if (list.contains(card)) {
                    if(card.getCardType().equals(CardType.CREDIT)){   //nqs eshte kart krediti po fshive karten do fshiet e accounti
                        cardRepository.deleteById(cardId);
                        accountRepository.deleteById(card.getAccount().getAccountId());
                        return "Card deleted";
                    }else {
                        cardRepository.deleteById(cardId);
                        return "Card deleted";
                    }
                } else {
                    throw new NoAuthorizationException("This user cannot access this card");
                }
            } else {
                throw new ResourceNotFoundException("This card doesn't exist");
            }
        }
        else {
            throw new ResourceNotFoundException("This user doesn't exist");
        }
    }
}
