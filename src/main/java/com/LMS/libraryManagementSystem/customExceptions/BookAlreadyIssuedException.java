package com.LMS.libraryManagementSystem.customExceptions;

public class BookAlreadyIssuedException extends Exception {
    public BookAlreadyIssuedException(String message){
        super(message);
    }
}
