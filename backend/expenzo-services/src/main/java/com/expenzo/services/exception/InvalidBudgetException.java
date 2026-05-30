package com.expenzo.services.exception;

public class InvalidBudgetException extends RuntimeException {

    public InvalidBudgetException(String message) {
        super(message);
    }
}
