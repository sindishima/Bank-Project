package com.example.Bank.Controller;

import com.example.Bank.Service.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log-out")
public class LogoutController {

    @Autowired
    LogoutService logoutService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLIENT', 'TELLER', 'ADMIN')")
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>(logoutService.logout(), HttpStatus.OK);
    }
}