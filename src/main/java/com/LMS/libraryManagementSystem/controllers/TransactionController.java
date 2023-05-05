package com.LMS.libraryManagementSystem.controllers;

import com.LMS.libraryManagementSystem.dtos.requestDtos.BookRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.BookResponseDto;
import com.LMS.libraryManagementSystem.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @PostMapping("/issue-book")
    public ResponseEntity issueBook(@RequestBody BookRequestDto bookRequestDto){
        try{
            BookResponseDto bookResponseDto = transactionService.issueBook(bookRequestDto);
            return new ResponseEntity<>(bookResponseDto, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/return-book/{utc}")
    public ResponseEntity returnBook(@PathVariable String utc){
        try{
            BookResponseDto bookResponseDto = transactionService.returnBook(utc);
            return new ResponseEntity<>(bookResponseDto, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-transaction-details/{cardId}/{bookId}")
    public ResponseEntity getTransactionDetails(@PathVariable Integer cardId, @PathVariable Integer bookId){
        try{
            List<BookResponseDto> transactionDetailsList = transactionService.getTransactionDetails(cardId, bookId);
            return new ResponseEntity<>(transactionDetailsList, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
