package com.expenzo.services.exception;

public class TransactionValidationFailedException extends RuntimeException {

    public TransactionValidationFailedException(String message) {
        super(message);
    }
}
