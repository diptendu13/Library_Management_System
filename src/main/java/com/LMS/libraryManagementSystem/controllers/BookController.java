package com.LMS.libraryManagementSystem.controllers;

import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksResponseDto;
import com.LMS.libraryManagementSystem.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    BookService bookService;

    @GetMapping("/find-books-by-genre")
    public ResponseEntity findBooksByGenre(@RequestParam String genre){
        try{
            List<FindAllBooksResponseDto> bookList = bookService.findBooksByGenre(genre);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find-books-by-publication")
    public ResponseEntity findBooksByPublication(@RequestParam String publication){
        try{
            List<FindAllBooksResponseDto> bookList = bookService.findBooksByPublication(publication);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-book/{bookId}")
    public ResponseEntity deleteBookById(@PathVariable int bookId){
        try{
            String message = bookService.deleteBookById(bookId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
