package com.example.Bank.Controller;

import com.example.Bank.Model.Transaction;
import com.example.Bank.Repostory.TransactionRepository;
import com.example.Bank.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@CrossOrigin(origins = {"http://localhost:3000"})
public class TransactionController {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAnyAuthority('TELLER', 'CLIENT')")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Integer transactionId){
        return new ResponseEntity<>(transactionService.getTransaction(transactionId), HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAnyAuthority('TELLER', 'CLIENT')")
    public ResponseEntity<List<Transaction>> getTransactionsFromOneAccount(@PathVariable Integer accountId){
        return new ResponseEntity<>(transactionService.getTransactionsFromOneAccount(accountId), HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('TELLER', 'CLIENT')")
    public ResponseEntity<List<Transaction>> getAllClientTransaction(@RequestParam(value = "clientId", required = false) Integer clientId){
        return new ResponseEntity<>(transactionService.getAllClientTransaction(clientId), HttpStatus.OK);
    }

    @PostMapping("/account/{senderAccount}")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Transaction> createTransaction(@RequestParam(value = "iban") String iban, @RequestParam("amount") Integer amount,
                                                         @PathVariable Integer senderAccount){
        return new ResponseEntity<>(transactionService.createTransaction(iban, amount, senderAccount), HttpStatus.CREATED);
    }
}
