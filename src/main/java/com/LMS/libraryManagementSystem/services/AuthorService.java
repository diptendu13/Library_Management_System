package com.LMS.libraryManagementSystem.services;

import com.LMS.libraryManagementSystem.dtos.requestDtos.AddBookRequestDto;
import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateAuthorDetailsRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.AddBookResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksOfAnAuthorResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateAuthorDetailsResponseDto;
import com.LMS.libraryManagementSystem.models.Author;

import java.util.List;

public interface AuthorService {
    public void addAuthor(Author author) throws Exception;

    public AddBookResponseDto addBook(AddBookRequestDto addBookRequestDto) throws Exception;

    public List<FindAllBooksOfAnAuthorResponseDto> findAllBooksOfAnAuthor(int authorId) throws Exception;

    public UpdateAuthorDetailsResponseDto updateAuthorDetails(UpdateAuthorDetailsRequestDto authorDetails) throws Exception;

    public String deleteAuthorById(int authorId) throws Exception;
}
