package com.LMS.libraryManagementSystem.customExceptions;

public class InvalidTransactionCodeException extends Exception {
    public InvalidTransactionCodeException(String message){
        super(message);
    }
}
