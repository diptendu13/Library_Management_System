package com.LMS.libraryManagementSystem.customExceptions;

public class BookAlreadyReturnedException extends Exception{
    public BookAlreadyReturnedException(String message){
        super(message);
    }
}
