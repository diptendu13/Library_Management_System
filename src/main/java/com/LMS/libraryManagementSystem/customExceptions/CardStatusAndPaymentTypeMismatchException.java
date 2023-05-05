package com.LMS.libraryManagementSystem.customExceptions;

public class CardStatusAndPaymentTypeMismatchException extends Exception {
    public CardStatusAndPaymentTypeMismatchException(String message){
        super(message);
    }
}
