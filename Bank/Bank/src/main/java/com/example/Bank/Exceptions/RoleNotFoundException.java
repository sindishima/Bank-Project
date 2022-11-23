package com.example.Bank.Exceptions;

public class RoleNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 2L;

    public RoleNotFoundException(String message) {
        super(message);
    }
}
