package com.example.Bank.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    public static Boolean LOGG_IN = false;

    public String logout() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        SecurityContextHolder.getContext().setAuthentication(null);
        auth.setAuthenticated(false);
        LOGG_IN = false;
        return "User is logged out";
    }
}