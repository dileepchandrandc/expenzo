package com.expenzo.services.exception;

public class InvalidExpenseCategoryException extends RuntimeException{

    public InvalidExpenseCategoryException(String name) {
        super(name);
    }
}
