package com.LMS.libraryManagementSystem.services;

import com.LMS.libraryManagementSystem.dtos.requestDtos.BookRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.BookResponseDto;

import java.util.List;

public interface TransactionService {

    public BookResponseDto issueBook(BookRequestDto bookRequestDto) throws Exception;

    public BookResponseDto returnBook(String utc) throws Exception;

    List<BookResponseDto> getTransactionDetails(int cardId, int bookId) throws Exception;
}
