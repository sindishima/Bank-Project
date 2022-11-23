package com.example.Bank.Controller;

import com.example.Bank.Enum.CardStatus;
import com.example.Bank.Enum.Currency;
import com.example.Bank.Model.Card;
import com.example.Bank.Repostory.CardRepository;
import com.example.Bank.Service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/card")
@CrossOrigin(origins = {"http://localhost:3000"})
public class CardController {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardService cardService;


    @GetMapping("/waitingList")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<Set<Integer>> getCardByStatus(@RequestParam(value = "cardStatus") CardStatus status) {
        return new ResponseEntity<>(cardService.getCardByStatus(status), HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<List<Card>> getAllCards(){
        return new ResponseEntity<>(cardService.getAllCards(), HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Card> getCard(@PathVariable Integer accountId){
        return new ResponseEntity<>(cardService.getCard(accountId), HttpStatus.OK);
    }

    @PostMapping("/debit/account/{accountId}")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Card> createDebitCard(@PathVariable Integer accountId, @Valid @RequestBody Card card){
        return new ResponseEntity<>(cardService.createDebitCard(accountId, card), HttpStatus.CREATED);
    }

    @PostMapping("/credit")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Card> createCreditCard(@RequestParam(value = "income") Integer income, @Valid @RequestBody Card card,
                                                 @RequestParam(value = "currency") Currency currency, @RequestParam(value = "bilance") Integer bilance) throws Exception {
        return new ResponseEntity<>(cardService.createCreditCard(income, card, currency, bilance), HttpStatus.CREATED);
    }

    @PutMapping("/{cardId}")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<Card> approveOrDeclineCard(@PathVariable Integer cardId, @RequestParam(value = "interest") Double interest,
                                            @RequestParam(value = "cardStatus") CardStatus cardStatus){
        return new ResponseEntity<>(cardService.approveOrDeclineCard(cardId, interest, cardStatus), HttpStatus.CREATED);
    }

    @PutMapping("/{cardId}/deposit")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Card> deposit(@RequestParam(value = "amount") Integer amount, @PathVariable Integer cardId){
        return new ResponseEntity<>(cardService.deposit(cardId, amount), HttpStatus.OK);
    }

    @PutMapping("/{cardId}/withdraw")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Card> withdraw(@RequestParam(value = "amount") Integer amount, @PathVariable Integer cardId){
        return new ResponseEntity<>(cardService.withdraw(cardId, amount), HttpStatus.OK);
    }

    @PutMapping("/{cardId}/activate")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<Card> activateCard(@PathVariable Integer cardId){
        return new ResponseEntity<>(cardService.activateCard(cardId), HttpStatus.OK);
    }

    @DeleteMapping("/{cardId}/delete")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<String> deleteCard(@PathVariable Integer cardId){
        return new ResponseEntity<>(cardService.deleteCard(cardId), HttpStatus.OK);
    }
}
