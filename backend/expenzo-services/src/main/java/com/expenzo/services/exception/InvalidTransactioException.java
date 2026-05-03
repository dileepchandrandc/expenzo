package com.expenzo.services.exception;

public class InvalidTransactioException extends RuntimeException {

    public InvalidTransactioException(String message) {
        super(message);
    }
}
