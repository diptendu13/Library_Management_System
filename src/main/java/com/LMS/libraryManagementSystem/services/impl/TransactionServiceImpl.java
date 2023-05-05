package com.LMS.libraryManagementSystem.services.impl;

import com.LMS.libraryManagementSystem.customExceptions.*;
import com.LMS.libraryManagementSystem.dtos.requestDtos.BookRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.BookResponseDto;
import com.LMS.libraryManagementSystem.enums.CardStatus;
import com.LMS.libraryManagementSystem.enums.TransactionStatus;
import com.LMS.libraryManagementSystem.enums.TransactionType;
import com.LMS.libraryManagementSystem.models.Book;
import com.LMS.libraryManagementSystem.models.Card;
import com.LMS.libraryManagementSystem.models.Transaction;
import com.LMS.libraryManagementSystem.repositorys.BookRepository;
import com.LMS.libraryManagementSystem.repositorys.CardRepository;
import com.LMS.libraryManagementSystem.repositorys.TransactionRepository;
import com.LMS.libraryManagementSystem.services.CardService;
import com.LMS.libraryManagementSystem.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    CardRepository cardRepository1;

    @Autowired
    BookRepository bookRepository1;

    @Autowired
    CardService cardService1;

    @Override
    public BookResponseDto issueBook(BookRequestDto bookRequestDto) throws Exception {

        // automatically refresh the card before any type of payment operation
        try{
            cardService1.refreshCard(bookRequestDto.getCardId());
        }
        catch (Exception e){
        }

        // create a new transaction
        Transaction transaction = new Transaction();

        // set some mandatory attributes of transaction
        transaction.setUniqueTransactionCode(String.valueOf(UUID.randomUUID()));
        transaction.setTransactionType(TransactionType.ISSUE);
        transaction.setDuration(bookRequestDto.getDurationInDays());

        // check if duration is within bounds
        if (bookRequestDto.getDurationInDays() > 3){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Duration_Out_Of_Bounds");
            transactionRepository.save(transaction);
            throw new DurationOutOfBoundsException("A book can be issued for a duration of maximum 21 Days. Select duration within bounds.");
        }

        // check if card id is valid
        Card card;
        try{
            card = cardRepository1.findById(bookRequestDto.getCardId()).get();
        }
        catch(Exception e){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Invalid_Card_Id");
            transactionRepository.save(transaction);
            throw new InvalidIdException("Invalid Card Id");
        }

        // if card id is valid set card to transaction object
        transaction.setCard(card);

        // check if book id is valid
        Book book;
        try{
            book = bookRepository1.findById(bookRequestDto.getBookId()).get();
        }
        catch(Exception e){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Invalid_Book_Id");
            transactionRepository.save(transaction);
            throw new InvalidIdException("Invalid Book Id");
        }

        // if book id is valid set book to transaction object
        transaction.setBook(book);

        // check if card is activated
        if (card.getCardStatus() != CardStatus.ACTIVATED){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Card_Not_Active");
            transactionRepository.save(transaction);
            throw new CardNotActiveException("Card Not Active! Activate or Renew Subscription.");
        }

        // check for any dues
        if (card.getDues() != 0){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Dues_Pending");
            transactionRepository.save(transaction);
            throw new DuesPendingException("Dues Pending! Clear dues to issue new book.");
        }

        // check if book is available
        if (book.isIssued()){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Book_Not_Available");
            transactionRepository.save(transaction);
            throw new BookAlreadyIssuedException("Book already issued. Not available at the moment.");
        }

        // if we reach here, that means cardId & bookId are valid, cardStatus is active and bookStatus is available
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setAdditionalInfo("Book_Issued_Successfully");
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // set book attributes that are needed to be set
        book.setIssued(true);
        book.setCard(card);
        book.getTransactionList().add(updatedTransaction);

        // set card attributes that are needed to be set
        card.getBookList().add(book);
        card.getTransactionList().add(updatedTransaction);

        // save card, book and transaction
        cardRepository1.save(card);

        // make response dto
        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .bookTitle(book.getTitle())
                .uniqueTransactionCode(updatedTransaction.getUniqueTransactionCode())
                .transactionDate(updatedTransaction.getTransactionDate().toString())
                .transactionType(updatedTransaction.getTransactionType().name())
                .transactionStatus(updatedTransaction.getTransactionStatus().name())
                .build();

        return bookResponseDto;
    }

    @Override
    public BookResponseDto returnBook(String utc) throws Exception {

        // create a new transaction
        Transaction transaction = new Transaction();

        // set some mandatory attributes of transaction
        transaction.setUniqueTransactionCode(String.valueOf(UUID.randomUUID()));
        transaction.setTransactionType(TransactionType.RETURN);
        transaction.setDuration(-1);

        Transaction issuedTransaction = transactionRepository.findByUniqueTransactionCode(utc);

        if (issuedTransaction == null){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Invalid_Transaction_Code");
            transactionRepository.save(transaction);
            throw new InvalidTransactionCodeException("Invalid Transaction Code");
        }

        // automatically refresh the card before any type of payment operation
        try{
            cardService1.refreshCard(issuedTransaction.getCard().getId());
        }
        catch (Exception e){
        }

        // utc is valid, so set card and book to transaction object
        Card card = issuedTransaction.getCard();
        Book book = issuedTransaction.getBook();

        transaction.setCard(card);
        transaction.setBook(book);

        // check card status
        if (card.getCardStatus() != CardStatus.ACTIVATED){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Card_Not_Active");
            transactionRepository.save(transaction);
            throw new CardNotActiveException("Card Not Active! Activate or Renew Subscription.");
        }

        // check if book is already returned
        if (!book.isIssued()){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setAdditionalInfo("Book_Already_Returned");
            transactionRepository.save(transaction);
            throw new BookAlreadyReturnedException("Issued Book Already Returned.");
        }

        // calculate fine and add it to dues if required
        LocalDateTime issueDate = issuedTransaction.getTransactionDate();
        int durationInDays = issuedTransaction.getDuration();

        LocalDateTime expectedReturnDate = issueDate.plusDays(durationInDays);
        LocalDateTime actualReturnDate = LocalDateTime.now();

        if (expectedReturnDate.isBefore(actualReturnDate)){
            long numberOfDays = expectedReturnDate.until(actualReturnDate, ChronoUnit.DAYS);
            int fineAmount = (int)(numberOfDays * 15);
            card.setDues(card.getDues() + fineAmount);
        }

        // finally set the transaction status and additional info
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setAdditionalInfo("Book_Returned_Successfully");
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // set required book attributes
        book.setIssued(false);
        book.getTransactionList().add(updatedTransaction);
        book.setCard(null);

        // set required card attributes
        card.getBookList().remove(book);
        card.getTransactionList().add(updatedTransaction);

        // save everything
        cardRepository1.save(card);

        // make response dto
        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .bookTitle(book.getTitle())
                .uniqueTransactionCode(updatedTransaction.getUniqueTransactionCode())
                .transactionDate(updatedTransaction.getTransactionDate().toString())
                .transactionType(updatedTransaction.getTransactionType().name())
                .transactionStatus(updatedTransaction.getTransactionStatus().name())
                .build();

        return bookResponseDto;
    }

    @Override
    public List<BookResponseDto> getTransactionDetails(int cardId, int bookId) throws Exception {

        List<Transaction> transactionList = transactionRepository.getTransactionListUsingCardIdAndBookId(cardId, bookId);

        if (transactionList.isEmpty()){
            throw new InvalidIdException("Invalid Card or Book Id");
        }

        // make response dto
        List<BookResponseDto> bookResponseDtoList = new ArrayList<>();
        for (Transaction transaction : transactionList){
            BookResponseDto bookResponseDto = BookResponseDto.builder()
                    .bookTitle(transaction.getBook().getTitle())
                    .uniqueTransactionCode(transaction.getUniqueTransactionCode())
                    .transactionDate(transaction.getTransactionDate().toString())
                    .transactionType(transaction.getTransactionType().name())
                    .transactionStatus(transaction.getTransactionStatus().name())
                    .build();

            bookResponseDtoList.add(bookResponseDto);
        }

        return bookResponseDtoList;
    }
}
