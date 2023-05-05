package com.LMS.libraryManagementSystem.services.impl;

import com.LMS.libraryManagementSystem.customExceptions.InvalidIdException;
import com.LMS.libraryManagementSystem.dtos.requestDtos.AddBookRequestDto;
import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateAuthorDetailsRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.AddBookResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksOfAnAuthorResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateAuthorDetailsResponseDto;
import com.LMS.libraryManagementSystem.models.Author;
import com.LMS.libraryManagementSystem.models.Book;
import com.LMS.libraryManagementSystem.repositorys.AuthorRepository;
import com.LMS.libraryManagementSystem.repositorys.BookRepository;
import com.LMS.libraryManagementSystem.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Override
    public void addAuthor(Author author) throws Exception {
        authorRepository.save(author);
    }

    @Override
    public AddBookResponseDto addBook(AddBookRequestDto addBookRequestDto) throws Exception {

        Author author;
        try{
            author = authorRepository.findById(addBookRequestDto.getAuthorId()).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Author Id");
        }

        Book book = Book.builder()
                .title(addBookRequestDto.getTitle())
                .genre(addBookRequestDto.getGenre())
                .numberOfPages(addBookRequestDto.getNumberOfPages())
                .publication(addBookRequestDto.getPublication())
                .price(addBookRequestDto.getPrice())
                .author(author)
                .isIssued(false)
                .build();

        author.getBookList().add(book);

        bookRepository.save(book);

        //Author updatedAuthor = authorRepository.save(author);

        // create the response dto
        AddBookResponseDto addBookResponseDto = AddBookResponseDto.builder()
                .message("Book has been added successfully!")
                .bookTitle(book.getTitle())
                .publication(book.getPublication())
                .authorName(author.getName())
                .price(book.getPrice())
                .build();

        return addBookResponseDto;
    }

    @Override
    public List<FindAllBooksOfAnAuthorResponseDto> findAllBooksOfAnAuthor(int authorId) throws Exception {
        Author author;
        try{
            author = authorRepository.findById(authorId).get();
        }
        catch(Exception e){
            throw new InvalidIdException("Invalid Author Id");
        }
        // form a bookList that we have to return
        List<FindAllBooksOfAnAuthorResponseDto> bookList = new ArrayList<>();

        // make the response dto
        for (Book book : author.getBookList()){
            FindAllBooksOfAnAuthorResponseDto bookDetails = FindAllBooksOfAnAuthorResponseDto.builder()
                    .bookTitle(book.getTitle())
                    .publication(book.getPublication())
                    .price(book.getPrice())
                    .build();
            bookList.add(bookDetails); // add response Dtos to bookList
        }

        return bookList;
    }

    @Override
    public UpdateAuthorDetailsResponseDto updateAuthorDetails(UpdateAuthorDetailsRequestDto authorDetails) throws Exception {

        Author author;
        try{
            author = authorRepository.findById(authorDetails.getAuthorId()).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Author Id");
        }
        // check which attributes to update
        if (authorDetails.getNewName() != null){
            author.setName(authorDetails.getNewName());
        }
        if (authorDetails.getNewAge() != 0){
            author.setAge(authorDetails.getNewAge());
        }
        if (authorDetails.getNewEmail() != null){
            author.setEmail(authorDetails.getNewEmail());
        }
        Author updatedAuthor = authorRepository.save(author);
        // make response Dto
        UpdateAuthorDetailsResponseDto updateAuthorDetailsResponseDto = UpdateAuthorDetailsResponseDto.builder()
                .name(updatedAuthor.getName())
                .age(updatedAuthor.getAge())
                .email(updatedAuthor.getEmail())
                .build();

        return updateAuthorDetailsResponseDto;
    }

    @Override
    public String deleteAuthorById(int authorId) throws Exception {

        Author author;
        try{
            author = authorRepository.findById(authorId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Author Id");
        }
        authorRepository.delete(author);

        return author.getName() + " deleted successfully!";
    }

}
