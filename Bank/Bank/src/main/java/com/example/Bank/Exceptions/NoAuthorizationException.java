package com.example.Bank.Exceptions;


public class NoAuthorizationException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public NoAuthorizationException(String message) {
        super(message);
    }
}
