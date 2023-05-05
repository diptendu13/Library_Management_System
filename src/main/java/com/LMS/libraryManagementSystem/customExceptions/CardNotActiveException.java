package com.LMS.libraryManagementSystem.customExceptions;

public class CardNotActiveException extends Exception {
    public CardNotActiveException(String message){
        super(message);
    }
}
