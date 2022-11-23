package com.example.Bank.Controller;

import com.example.Bank.Enum.AccountStatus;
import com.example.Bank.Model.Account;
import com.example.Bank.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AccountController {

    @Autowired
    AccountService accountService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public List<Account> getAllAccountsFromAllUsers(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TELLER', 'CLIENT')")
    public ResponseEntity<Account> getAccount(@PathVariable Integer id){
        return new ResponseEntity<>(accountService.getAccount(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('TELLER', 'CLIENT')")
    public ResponseEntity<List<Account>> getAllAccountsOfAClient(@RequestParam(value = "clientId", required = false) Integer clientId){
        return new ResponseEntity<>(accountService.getAllAccountsOfAClient(clientId), HttpStatus.OK);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<List<Account>> getAccountsByStatus(@RequestParam(value = "accountStatus", required = false) AccountStatus accountStatus){
        return new ResponseEntity<>(accountService.getAccountsByStatus(accountStatus), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) throws Exception {
        return new ResponseEntity<>(accountService.createAccount(account), HttpStatus.CREATED);
    }


    @PutMapping("/{accountId}/updateStatus")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<String> approveOrDeclineAccount(@PathVariable Integer accountId, @RequestParam(value = "accountStatus") AccountStatus accountStatus){
        return new ResponseEntity<>(accountService.approveOrDeclineAccount(accountId, accountStatus), HttpStatus.OK);
    }

    @PutMapping("/{accountId}/activate")
    @PreAuthorize("hasAnyAuthority('TELLER')")
    public ResponseEntity<Account> activateAccount(@PathVariable Integer accountId){
        return new ResponseEntity<>(accountService.activateAccount(accountId), HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<String> deleteAccount(@PathVariable Integer accountId){
        return new ResponseEntity<>(accountService.deleteAccount(accountId), HttpStatus.OK);
    }
}
