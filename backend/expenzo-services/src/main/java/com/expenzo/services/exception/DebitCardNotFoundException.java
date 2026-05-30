package com.expenzo.services.exception;

public class DebitCardNotFoundException extends RuntimeException {

    public DebitCardNotFoundException(String message) {
        super(message);
    }
}
