package org.bank.exception;

public class AccountAlreadyClosedException extends RuntimeException {
    public AccountAlreadyClosedException(String message) {
        super(message);
    }
}
