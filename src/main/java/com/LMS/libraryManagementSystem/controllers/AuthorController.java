package com.LMS.libraryManagementSystem.controllers;

import com.LMS.libraryManagementSystem.dtos.requestDtos.AddBookRequestDto;
import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateAuthorDetailsRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.AddBookResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksOfAnAuthorResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateAuthorDetailsResponseDto;
import com.LMS.libraryManagementSystem.models.Author;
import com.LMS.libraryManagementSystem.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @PostMapping("/add")
    public ResponseEntity addAuthor(@RequestBody Author author) {
        try{
            authorService.addAuthor(author);
            return new ResponseEntity<>("Author Added Successfully!", HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Unable To Add! Try Again Later.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-book")
    public ResponseEntity addBook(@RequestBody AddBookRequestDto addBookRequestDto) {
        try{
            AddBookResponseDto addedBook = authorService.addBook(addBookRequestDto);
            return new ResponseEntity<>(addedBook, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find-all-books-of-an-author/{authorId}")
    public ResponseEntity findAllBooksOfAnAuthor(@PathVariable int authorId){
        try{
            List<FindAllBooksOfAnAuthorResponseDto> findAllBooks = authorService.findAllBooksOfAnAuthor(authorId);
            return new ResponseEntity<>(findAllBooks, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-author-details")
    public ResponseEntity updateAuthorDetails(@RequestBody UpdateAuthorDetailsRequestDto authorDetails){
        try{
            UpdateAuthorDetailsResponseDto updatedAuthor = authorService.updateAuthorDetails(authorDetails);
            return new ResponseEntity<>(updatedAuthor, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-author/{authorId}")
    public ResponseEntity deleteAuthorById(@PathVariable int authorId){
        try{
            String message = authorService.deleteAuthorById(authorId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}
