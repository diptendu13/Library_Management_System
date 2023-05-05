package com.LMS.libraryManagementSystem.services.impl;

import com.LMS.libraryManagementSystem.customExceptions.EmptyListException;
import com.LMS.libraryManagementSystem.customExceptions.InvalidGenreException;
import com.LMS.libraryManagementSystem.customExceptions.InvalidIdException;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksResponseDto;
import com.LMS.libraryManagementSystem.enums.Genre;
import com.LMS.libraryManagementSystem.models.Book;
import com.LMS.libraryManagementSystem.repositorys.BookRepository;
import com.LMS.libraryManagementSystem.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository bookRepository;

    @Override
    public List<FindAllBooksResponseDto> findBooksByGenre(String genre) throws Exception {
        boolean genreExists = false;
        Genre givenGenre = Genre.FICTION;
        for (Genre genreValue : Genre.values()){
            if (genreValue.name().equalsIgnoreCase(genre)){
                givenGenre = genreValue;
                genreExists = true;
                break;
            }
        }
        if (!genreExists){
            throw new InvalidGenreException("Invalid Genre.\nSelect from :\n"+
                    "    [FICTION,\n" +
                    "    NON_FICTION,\n" +
                    "    SCI_FI,\n" +
                    "    THRILLER,\n" +
                    "    SELF_HELP,\n" +
                    "    SPIRITUAL,\n" +
                    "    HORROR,\n" +
                    "    MYSTERY,\n" +
                    "    BIOGRAPHY,\n" +
                    "    COMIC,\n" +
                    "    POETRY]");
        }
        // get books of given genre
        List<Book> bookList = bookRepository.findByGenre(givenGenre);

        if (bookList.isEmpty()){
            throw new EmptyListException("No books found under corresponding genre");
        }

        // make response dto
        List<FindAllBooksResponseDto> findAllBooksResponseDtoList = new ArrayList<>();

        for (Book book : bookList){
            FindAllBooksResponseDto eachBook = FindAllBooksResponseDto.builder()
                    .bookTitle(book.getTitle())
                    .publication(book.getPublication())
                    .authorName(book.getAuthor().getName())
                    .genre(book.getGenre().name())
                    .price(book.getPrice())
                    .build();

            findAllBooksResponseDtoList.add(eachBook);
        }
        return findAllBooksResponseDtoList;
    }

    @Override
    public List<FindAllBooksResponseDto> findBooksByPublication(String publication) throws Exception {

        // get books of given publication
        List<Book> bookList = bookRepository.findByPublication(publication);
        if (bookList.isEmpty()){
            throw new EmptyListException("No books found under corresponding publication");
        }
        // make response dto
        List<FindAllBooksResponseDto> findAllBooksResponseDtoList = new ArrayList<>();

        for (Book book : bookList){
            FindAllBooksResponseDto eachBook = FindAllBooksResponseDto.builder()
                    .bookTitle(book.getTitle())
                    .publication(book.getPublication())
                    .authorName(book.getAuthor().getName())
                    .genre(book.getGenre().name())
                    .price(book.getPrice())
                    .build();

            findAllBooksResponseDtoList.add(eachBook);
        }
        return findAllBooksResponseDtoList;
    }

    @Override
    public String deleteBookById(int bookId) throws Exception {

        Book book;
        try{
            book = bookRepository.findById(bookId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Book Id");
        }
        bookRepository.delete(book);

        return book.getTitle() + " deleted successfully!";
    }
}
